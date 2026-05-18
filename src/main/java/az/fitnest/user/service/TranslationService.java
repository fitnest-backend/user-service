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

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TranslationService.class);

    @org.springframework.beans.factory.annotation.Value("${LIBRETRANSLATE_URL:http://10.0.0.4:5000}")
    private String libreTranslateUrl;

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
        // Try Google Translate first (Ultra-accurate, extremely reliable, free, no keys needed)
        try {
            String googleTranslated = translateWithGoogle(text, targetLanguage);
            if (googleTranslated != null && !googleTranslated.trim().isEmpty()) {
                log.info("Translation successful using Google Translate [AZ -> {}]: '{}' -> '{}'", 
                    targetLanguage.toUpperCase(), text, googleTranslated);
                return googleTranslated;
            }
        } catch (Exception e) {
            log.warn("Google Translate failed, falling back to LibreTranslate. Error: {}", e.getMessage());
        }

        // Fallback to LibreTranslate
        return translateWithLibreTranslate(text, targetLanguage);
    }

    private String translateWithGoogle(String text, String targetLanguage) {
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=az&tl=" 
                + targetLanguage.toLowerCase() + "&dt=t&q=" 
                + java.net.URLEncoder.encode(text, java.nio.charset.StandardCharsets.UTF_8);

            log.info("Google Translate Request [AZ -> {}]: '{}'", targetLanguage.toUpperCase(), text);
            String response = restTemplate.getForObject(url, String.class);
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

    private String translateWithLibreTranslate(String text, String targetLanguage) {
        log.info("LibreTranslate API Request: url='{}/translate', text='{}', source='az', target='{}'", 
            libreTranslateUrl, text, targetLanguage);
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

            String requestUrl = libreTranslateUrl + "/translate";
            org.springframework.http.ResponseEntity<java.util.Map> response = restTemplate.postForEntity(
                requestUrl,
                request,
                java.util.Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String translatedText = (String) response.getBody().get("translatedText");
                log.info("LibreTranslate API Response: HTTP status={}, translatedText='{}'", 
                    response.getStatusCode(), translatedText);
                return translatedText;
            } else {
                log.warn("LibreTranslate API Response returned non-success status code: HTTP status={}, body={}", 
                    response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("LibreTranslate API Request FAILED: url='{}/translate', text='{}', targetLanguage='{}'. Error: {}", 
                libreTranslateUrl, text, targetLanguage, e.getMessage(), e);
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
