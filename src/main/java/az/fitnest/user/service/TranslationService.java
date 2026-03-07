package az.fitnest.user.service;

import az.fitnest.user.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TranslationService {

    private final TranslationRepository translationRepository;

    public String getTranslatedValue(String entityType, String entityId, String fieldName, String userLanguage) {
        return translationRepository.findByEntityTypeAndEntityIdAndLanguageCodeAndFieldName(entityType, entityId, userLanguage, fieldName)
                .map(translation -> translation.getFieldValue())
                .orElseGet(() ->
                        translationRepository.findByEntityTypeAndEntityIdAndLanguageCodeAndFieldName(entityType, entityId, "AZ", fieldName)
                                .map(translation -> translation.getFieldValue())
                                .orElse("")
                );
    }
}
