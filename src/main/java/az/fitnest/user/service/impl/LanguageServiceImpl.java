package az.fitnest.user.service.impl;

import az.fitnest.user.dto.request.LanguageCreateRequest;
import az.fitnest.user.dto.response.LanguageDto;
import az.fitnest.user.model.entity.Language;
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
    public LanguageDto getLanguageByCode(String code) {
        Language language = languageRepository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("error.resource_not_found"));
        return toDto(language);
    }

    @Transactional
    @Override
    public LanguageDto createLanguage(LanguageCreateRequest request) {
        Language language = Language.builder()
                .code(request.code().toUpperCase())
                .build();
        language = languageRepository.save(language);
        return toDto(language);
    }

    @Transactional
    @Override
    public LanguageDto updateLanguage(String code, LanguageCreateRequest request) {
        Language language = languageRepository.findById(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("error.resource_not_found"));
        String newCode = request.code().toUpperCase();
        if (!language.getCode().equals(newCode) && languageRepository.existsByCode(newCode)) {
            throw new BadRequestException("error.resource_already_exists");
        }
        language.setCode(newCode);
        language = languageRepository.save(language);
        return toDto(language);
    }

    @Transactional
    @Override
    public void deleteLanguage(String code) {
        if (!languageRepository.existsById(code)) {
            throw new ResourceNotFoundException("error.resource_not_found");
        }
        languageRepository.deleteById(code);
    }

    private LanguageDto toDto(Language language) {
        return LanguageDto.builder()
                .code(language.getCode())
                .build();
    }
}
