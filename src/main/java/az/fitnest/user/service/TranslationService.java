package az.fitnest.user.service;

import az.fitnest.user.model.entity.Translation;
import az.fitnest.user.repository.TranslationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TranslationService {

    private final TranslationRepository translationRepository;

    public String getTranslatedValue(String entityType, String entityId, String fieldName, String userLanguage) {
        if (userLanguage == null || userLanguage.equalsIgnoreCase("AZ")) {
            return null;
        }

        return translationRepository.findByEntityTypeAndEntityIdAndLanguageCodeAndFieldName(
                entityType,
                entityId,
                userLanguage.toUpperCase(),
                fieldName
        )
        .map(Translation::getFieldValue)
        .orElse(null);
    }

    @org.springframework.beans.factory.annotation.Value("${LIBRETRANSLATE_URL:http://10.0.0.4:5000}")
    private String libreTranslateUrl;

    public void autoTranslateAndSave(String entityType, String entityId, String fieldName, String originalValueAz) {
        if (originalValueAz == null || originalValueAz.trim().isEmpty()) {
            return;
        }

        // Translate to EN
        String enValue = translateText(originalValueAz, "en");
        if (enValue == null || enValue.trim().isEmpty()) {
            enValue = originalValueAz;
        }
        saveOrUpdateTranslation(entityType, entityId, "EN", fieldName, enValue);

        // Translate to RU
        String ruValue = translateText(originalValueAz, "ru");
        if (ruValue == null || ruValue.trim().isEmpty()) {
            ruValue = originalValueAz;
        }
        saveOrUpdateTranslation(entityType, entityId, "RU", fieldName, ruValue);
    }

    private String translateText(String text, String targetLanguage) {
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

            org.springframework.util.LinkedMultiValueMap<String, String> map = new org.springframework.util.LinkedMultiValueMap<>();
            map.add("q", text);
            map.add("source", "az");
            map.add("target", targetLanguage.toLowerCase());

            org.springframework.http.HttpEntity<org.springframework.util.LinkedMultiValueMap<String, String>> request = 
                new org.springframework.http.HttpEntity<>(map, headers);

            org.springframework.http.ResponseEntity<java.util.Map> response = restTemplate.postForEntity(
                libreTranslateUrl + "/translate",
                request,
                java.util.Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("translatedText");
            }
        } catch (Exception e) {
            System.err.println("LibreTranslate failed: " + e.getMessage());
        }
        return null;
    }

    private void saveOrUpdateTranslation(String entityType, String entityId, String languageCode, String fieldName, String fieldValue) {
        String normalizedEntityType = entityType;
        String normalizedLanguageCode = languageCode.toUpperCase();

        Translation existing = translationRepository.findByEntityTypeAndEntityIdAndLanguageCodeAndFieldName(
                normalizedEntityType, entityId, normalizedLanguageCode, fieldName
        ).orElse(null);

        if (existing != null) {
            existing.setFieldValue(fieldValue);
            translationRepository.save(existing);
        } else {
            Translation translation = Translation.builder()
                    .entityType(normalizedEntityType)
                    .entityId(entityId)
                    .languageCode(normalizedLanguageCode)
                    .fieldName(fieldName)
                    .fieldValue(fieldValue)
                    .build();
            translationRepository.save(translation);
        }
    }
}
