package az.fitnest.user.service.impl;

import az.fitnest.user.dto.LanguageCreateRequest;
import az.fitnest.user.dto.LanguageDto;
import az.fitnest.user.entity.Language;
import az.fitnest.user.exception.BadRequestException;
import az.fitnest.user.exception.ResourceNotFoundException;
import az.fitnest.user.repository.LanguageRepository;
import az.fitnest.user.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;

    @Override
    public List<LanguageDto> getAllLanguages() {
        return languageRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public LanguageDto getLanguageById(Long id) {
        Language language = languageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Language not found"));
        return toDto(language);
    }

    @Transactional
    @Override
    public LanguageDto createLanguage(LanguageCreateRequest request) {
        if (languageRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Language code already exists");
        }
        Language language = Language.builder()
                .code(request.getCode())
                .build();
        language = languageRepository.save(language);
        return toDto(language);
    }

    @Transactional
    @Override
    public LanguageDto updateLanguage(Long id, LanguageCreateRequest request) {
        Language language = languageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Language not found"));
        if (!language.getCode().equals(request.getCode()) && languageRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Language code already exists");
        }
        language.setCode(request.getCode());
        language = languageRepository.save(language);
        return toDto(language);
    }

    @Transactional
    @Override
    public void deleteLanguage(Long id) {
        if (!languageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Language not found");
        }
        languageRepository.deleteById(id);
    }

    private LanguageDto toDto(Language language) {
        return LanguageDto.builder()
                .id(language.getId())
                .code(language.getCode())
                .build();
    }
}
