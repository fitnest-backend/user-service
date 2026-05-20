package az.fitnest.user.service;

import az.fitnest.user.model.entity.Translation;
import az.fitnest.user.repository.TranslationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.scheduling.annotation.Async;

@Service
public class TranslationService {

    private final TranslationRepository translationRepository;
    private final org.springframework.web.client.RestTemplate restTemplate;

    public TranslationService(TranslationRepository translationRepository) {
        this.translationRepository = translationRepository;
        org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(1000);
        factory.setReadTimeout(1500);
        this.restTemplate = new org.springframework.web.client.RestTemplate(factory);
    }

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

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TranslationService.class);

    @Async
    public void autoTranslateAndSave(String entityType, String entityId, String fieldName, String originalValueAz) {
        if (originalValueAz == null || originalValueAz.trim().isEmpty()) {
            log.warn("Auto-translation skipped: originalValueAz is null or empty for entityType={}, entityId={}, fieldName={}", 
                entityType, entityId, fieldName);
            return;
        }

        log.info("Starting auto-translation process for entityType={}, entityId={}, fieldName={}, originalValueAz='{}'", 
            entityType, entityId, fieldName, originalValueAz);

        // Translate to EN
        String enValue = translateText(originalValueAz, "en");
        if (enValue != null && !enValue.trim().isEmpty()) {
            log.info("Auto-translated [AZ -> EN] success. Value: '{}'", enValue);
            saveOrUpdateTranslation(entityType, entityId, "EN", fieldName, enValue);
        } else {
            log.warn("Auto-translation [AZ -> EN] returned empty or null value. Using fallback: '{}'", originalValueAz);
            saveOrUpdateTranslation(entityType, entityId, "EN", fieldName, originalValueAz);
        }

        // Translate to RU
        String ruValue = translateText(originalValueAz, "ru");
        if (ruValue != null && !ruValue.trim().isEmpty()) {
            log.info("Auto-translated [AZ -> RU] success. Value: '{}'", ruValue);
            saveOrUpdateTranslation(entityType, entityId, "RU", fieldName, ruValue);
        } else {
            log.warn("Auto-translation [AZ -> RU] returned empty or null value. Using fallback: '{}'", originalValueAz);
            saveOrUpdateTranslation(entityType, entityId, "RU", fieldName, originalValueAz);
        }
    }

    private String translateText(String text, String targetLanguage) {
        // Try Google Translate (Ultra-accurate, extremely reliable, free, no keys needed)
        try {
            String googleTranslated = translateWithGoogle(text, targetLanguage);
            if (googleTranslated != null && !googleTranslated.trim().isEmpty()) {
                log.info("Translation successful using Google Translate [AZ -> {}]: '{}' -> '{}'", 
                    targetLanguage.toUpperCase(), text, googleTranslated);
                return googleTranslated;
            }
        } catch (Exception e) {
            log.error("Google Translate failed. Error: {}", e.getMessage());
        }
        return null;
    }

    private String translateWithGoogle(String text, String targetLanguage) {
        try {
            java.net.URI uri = org.springframework.web.util.UriComponentsBuilder
                .fromUriString("https://translate.googleapis.com/translate_a/single")
                .queryParam("client", "gtx")
                .queryParam("sl", "az")
                .queryParam("tl", targetLanguage.toLowerCase())
                .queryParam("dt", "t")
                .queryParam("q", text)
                .build()
                .toUri();

            log.info("Google Translate Request [AZ -> {}]: '{}'", targetLanguage.toUpperCase(), text);
            String response = restTemplate.getForObject(uri, String.class);
            if (response != null) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(response);
                if (rootNode.isArray() && rootNode.size() > 0) {
                    com.fasterxml.jackson.databind.JsonNode firstArray = rootNode.get(0);
                    if (firstArray.isArray() && firstArray.size() > 0) {
                        com.fasterxml.jackson.databind.JsonNode translationPair = firstArray.get(0);
                        if (translationPair.isArray() && translationPair.size() > 0) {
                            return translationPair.get(0).asText();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Google Translation API failed for text '{}' to '{}': {}", text, targetLanguage, e.getMessage());
        }
        return null;
    }

    private void saveOrUpdateTranslation(String entityType, String entityId, String languageCode, String fieldName, String fieldValue) {
        String normalizedEntityType = entityType;
        String normalizedLanguageCode = languageCode.toUpperCase();

        log.info("Database Save: entityType={}, entityId={}, languageCode={}, fieldName={}, fieldValue='{}'", 
            normalizedEntityType, entityId, normalizedLanguageCode, fieldName, fieldValue);

        Translation existing = translationRepository.findByEntityTypeAndEntityIdAndLanguageCodeAndFieldName(
                normalizedEntityType, entityId, normalizedLanguageCode, fieldName
        ).orElse(null);

        if (existing != null) {
            log.info("Updating existing translation record ID={}", existing.getId());
            existing.setFieldValue(fieldValue);
            translationRepository.save(existing);
        } else {
            log.info("Creating new translation record");
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
