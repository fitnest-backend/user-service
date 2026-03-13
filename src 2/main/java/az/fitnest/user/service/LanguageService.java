package az.fitnest.user.service;

import az.fitnest.user.dto.request.LanguageCreateRequest;
import az.fitnest.user.dto.response.LanguageDto;
import java.util.List;

public interface LanguageService {
    List<LanguageDto> getAllLanguages();
    LanguageDto getLanguageById(Long id);
    LanguageDto createLanguage(LanguageCreateRequest request);
    LanguageDto updateLanguage(Long id, LanguageCreateRequest request);
    void deleteLanguage(Long id);
}
