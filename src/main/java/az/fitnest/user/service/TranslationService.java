package az.fitnest.user.service;

import az.fitnest.user.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TranslationService {

    private final TranslationRepository translationRepository;

    public String getTranslatedValue(String entityType, String entityId, String fieldName, String userLanguage) {
        // Try user language first
        return translationRepository.findByEntityTypeAndEntityIdAndLanguageCodeAndFieldName(entityType, entityId, userLanguage, fieldName)
                .map(translation -> translation.getFieldValue())
                .orElseGet(() ->
                        // Fallback to Azerbaijan
                        translationRepository.findByEntityTypeAndEntityIdAndLanguageCodeAndFieldName(entityType, entityId, "AZ", fieldName)
                                .map(translation -> translation.getFieldValue())
                                .orElse("")
                );
    }
}
