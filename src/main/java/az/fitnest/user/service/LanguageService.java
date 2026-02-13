package az.fitnest.user.service;

import az.fitnest.user.dto.LanguageCreateRequest;
import az.fitnest.user.dto.LanguageDto;
import java.util.List;

public interface LanguageService {
    List<LanguageDto> getAllLanguages();
    LanguageDto getLanguageById(Long id);
    LanguageDto createLanguage(LanguageCreateRequest request);
    LanguageDto updateLanguage(Long id, LanguageCreateRequest request);
    void deleteLanguage(Long id);
}
