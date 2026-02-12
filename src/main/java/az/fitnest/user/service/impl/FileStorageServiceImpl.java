package az.fitnest.user.service.impl;
import az.fitnest.user.service.*;

import az.fitnest.user.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;
import java.util.Arrays;

/**
 * Service for handling file storage operations.
 * Manages file uploads, validation, and deletion through the media service.
 *
 * <p>This service enforces file size limits and content type restrictions
 * to ensure only valid images are uploaded.
 *
 * @see TeraBoxGrpcClient
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    /** Maximum allowed file size: 5MB */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /** List of allowed image content types */
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png"
    );

    @Value("${terabox.worker.url:http://terabox-worker-service:9090}")
    private String teraboxWorkerUrl;

    private final WebClient webClient = WebClient.create();

    /**
     * Saves a file to the media service via gRPC.
     *
     * @param file the multipart file to save
     * @return the URL of the uploaded file, or null if file is empty
     * @throws BadRequestException if file validation fails or upload fails
     */
    @Override
    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateFile(file);

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());
            body.add("directory", "/uploads");

            JsonNode response = webClient.post()
                    .uri(teraboxWorkerUrl + "/api/v1/upload/upload")
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.get("success").asBoolean() && response.has("data")) {
                String imageUrl = response.get("data").get("path").asText();
                return imageUrl;
            } else {
                log.error("Upload failed via HTTP: {}", response != null ? response.get("message").asText() : "Unknown error");
                throw new BadRequestException("Failed to upload profile image");
            }
        } catch (Exception e) {
            log.error("Error calling terabox-worker HTTP service: ", e);
            throw new BadRequestException("Failed to upload profile image: " + e.getMessage());
        }
    }

    /**
     * Validates file before upload.
     * Checks content type and file size against configured limits.
     *
     * @param file the file to validate
     * @throws BadRequestException if file is invalid
     */
    private void validateFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Only JPEG and PNG images are allowed");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds maximum allowed size of 5MB");
        }
    }

    /**
     * Deletes a single file from the media service via gRPC.
     *
     * @param fileUrl the URL of the file to delete
     */
    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return;
        }
        deleteFiles(List.of(fileUrl));
    }

    /**
     * Deletes multiple files from the media service via gRPC.
     * Errors are logged but not thrown to avoid blocking the main flow.
     *
     * @param fileUrls list of file URLs to delete
     */
    @Override
    public void deleteFiles(List<String> fileUrls) {
        if (fileUrls == null || fileUrls.isEmpty()) {
            return;
        }
        // TODO: Implement HTTP delete if needed
        log.warn("Delete files not implemented via HTTP: {}", fileUrls);
    }
}
