package az.fitnest.user.service;

import az.fitnest.user.dto.request.LanguageCreateRequest;
import az.fitnest.user.dto.response.LanguageDto;

import java.util.List;

public interface LanguageService {
    List<LanguageDto> getAllLanguages();

    LanguageDto getLanguageByCode(String code);

    LanguageDto createLanguage(LanguageCreateRequest request);

    LanguageDto updateLanguage(String code, LanguageCreateRequest request);

    void deleteLanguage(String code);
}
