package az.fitnest.user.service.impl;

import az.fitnest.user.client.CachedIdentityGrpcClient;
import az.fitnest.user.client.StorageGrpcClient;
import az.fitnest.user.dto.response.GoalItemResponse;
import az.fitnest.user.exception.BadRequestException;
import az.fitnest.user.exception.ConflictException;
import az.fitnest.user.exception.ResourceNotFoundException;
import az.fitnest.user.model.entity.GoalReference;
import az.fitnest.user.model.entity.Translation;
import az.fitnest.user.repository.GoalReferenceRepository;
import az.fitnest.user.repository.TranslationRepository;
import az.fitnest.user.service.FileStorageService;
import az.fitnest.user.service.GoalReferenceService;
import az.fitnest.user.service.TranslationService;
import az.fitnest.user.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalReferenceServiceImpl implements GoalReferenceService {

    private final GoalReferenceRepository goalReferenceRepository;
    private final TranslationRepository translationRepository;
    private final FileStorageService fileStorageService;
    private final CachedIdentityGrpcClient cachedIdentityGrpcClient;
    private final TranslationService translationService;
    private final StorageGrpcClient storageGrpcClient;

    @Override
    public List<GoalItemResponse> getAllGoals() {
        String userLanguage = getUserLanguage();
        List<GoalReference> goals = goalReferenceRepository.findAllByOrderByGoalCodeAsc();
        return goals.stream().map(goal -> mapToResponse(goal, userLanguage)).collect(Collectors.toList());
    }

    @Override
    public GoalItemResponse getGoalByCode(String code) {
        GoalReference goal = goalReferenceRepository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("error.goal_reference_not_found"));
        return mapToResponse(goal, getUserLanguage());
    }

    @Override
    public StreamingResponseBody streamGoalImage(String fsId) {
        return outputStream -> {
            storageGrpcClient.downloadFile(fsId, response -> {
                if (response.hasFileData()) {
                    try {
                        outputStream.write(response.getFileData().toByteArray());
                    } catch (IOException e) {
                    }
                }
            });
            try {
                outputStream.flush();
            } catch (IOException e) {
            }
        };
    }

    @Transactional
    @Override
    public GoalReference createGoal(String code, String title, String subtitle) {
        if (goalReferenceRepository.existsById(code)) {
            throw new ConflictException("error.resource_already_exists");
        }
        GoalReference goal = new GoalReference();
        goal.setGoalCode(code);
        goalReferenceRepository.save(goal);

        createTranslationIfNotFound(code, "EN", title, subtitle);
        createTranslationIfNotFound(code, "AZ", title, subtitle);
        createTranslationIfNotFound(code, "RU", title, subtitle);

        return goal;
    }

    @Transactional
    @Override
    public GoalReference updateGoal(String code, String title, String subtitle) {
        GoalReference goal = goalReferenceRepository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("error.goal_reference_not_found"));

        updateOrSaveTranslation(code, "EN", "title", title);
        updateOrSaveTranslation(code, "EN", "subtitle", subtitle);

        return goal;
    }

    @Transactional
    @Override
    public void deleteGoal(String code) {
        GoalReference goal = goalReferenceRepository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("error.goal_reference_not_found"));

        if (goal.getImageUrl() != null && !goal.getImageUrl().isBlank()) {
            try {
                fileStorageService.deleteFile(goal.getImageUrl());
            } catch (Exception e) {
            }
        }

        goalReferenceRepository.deleteById(code);
    }

    @Transactional
    @Override
    public void uploadGoalImage(String code, MultipartFile file) {
        GoalReference goal = goalReferenceRepository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("error.goal_reference_not_found"));

        validateImage(file);
        String imageUrl = fileStorageService.saveFile(file, "/goals", goal.getImageUrl());
        goal.setImageUrl(imageUrl);
        goalReferenceRepository.save(goal);
    }

    private GoalItemResponse mapToResponse(GoalReference goal, String userLanguage) {
        String title = translationService.getTranslatedValue("GoalReference", goal.getGoalCode(), "title", userLanguage);
        String subtitle = translationService.getTranslatedValue("GoalReference", goal.getGoalCode(), "subtitle", userLanguage);
        return GoalItemResponse.builder()
                .code(goal.getGoalCode())
                .title(title)
                .subtitle(subtitle)
                .imageUrl(getFullImageUrl(goal.getImageUrl()))
                .build();
    }

    private void updateOrSaveTranslation(String entityId, String lang, String field, String value) {
        translationRepository.findByEntityTypeAndEntityIdAndLanguageCodeAndFieldName("GoalReference", entityId, lang, field)
                .ifPresentOrElse(t -> {
                    t.setFieldValue(value);
                    translationRepository.save(t);
                }, () -> {
                    Translation t = Translation.builder()
                            .entityType("GoalReference")
                            .entityId(entityId)
                            .languageCode(lang)
                            .fieldName(field)
                            .fieldValue(value)
                            .build();
                    translationRepository.save(t);
                });
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BadRequestException("error.file_required");
        if (file.getSize() > 5 * 1024 * 1024) throw new BadRequestException("error.file_size_limit");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/"))
            throw new BadRequestException("error.only_images_allowed");
    }

    private void createTranslationIfNotFound(String goalCode, String languageCode, String title, String subtitle) {
        if (!translationRepository.existsByEntityTypeAndEntityIdAndLanguageCodeAndFieldName("GoalReference", goalCode, languageCode, "title")) {
            translationRepository.save(Translation.builder().entityType("GoalReference").entityId(goalCode).languageCode(languageCode).fieldName("title").fieldValue(title).build());
        }
        if (!translationRepository.existsByEntityTypeAndEntityIdAndLanguageCodeAndFieldName("GoalReference", goalCode, languageCode, "subtitle")) {
            translationRepository.save(Translation.builder().entityType("GoalReference").entityId(goalCode).languageCode(languageCode).fieldName("subtitle").fieldValue(subtitle).build());
        }
    }

    private String getUserLanguage() {
        Long userId = UserContext.getCurrentUserId();
        if (userId != null) {
            try {
                return cachedIdentityGrpcClient.getUserById(userId).language().toUpperCase();
            } catch (Exception e) {
            }
        }
        return "AZ";
    }

    private String getFullImageUrl(String fsId) {
        if (fsId == null || fsId.trim().isEmpty()) return null;
        return "/api/v1/goals/images/" + fsId;
    }
}
