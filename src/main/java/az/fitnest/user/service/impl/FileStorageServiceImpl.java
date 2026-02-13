package az.fitnest.user.service.impl;

import az.fitnest.user.client.TeraBoxWorkerClient;
import az.fitnest.user.dto.media.MediaUploadResponse;
import az.fitnest.user.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * Service for handling file storage operations.
 * Manages file uploads and deletion through the terabox-worker service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements az.fitnest.user.service.FileStorageService {

    /** Maximum allowed file size: 5MB */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /** List of allowed image content types */
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png"
    );

    private final TeraBoxWorkerClient teraBoxWorkerClient;

    /**
     * Saves a file to the terabox-worker service.
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
            log.debug("Uploading file {} to terabox-worker", file.getOriginalFilename());
            ResponseEntity<MediaUploadResponse> responseEntity = teraBoxWorkerClient.uploadFile(file, "/uploads");
            
            MediaUploadResponse response = responseEntity.getBody();

            if (response != null && response.isSuccess() && response.getData() != null) {
                String imageUrl = response.getData().getPath();
                log.debug("Successfully uploaded file: {}", imageUrl);
                return imageUrl;
            } else {
                log.error("Upload failed: {}", response != null ? response.getMessage() : "Unknown error");
                throw new BadRequestException("Failed to upload profile image");
            }
        } catch (Exception e) {
            log.error("Error calling terabox-worker service: ", e);
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
     * Deletes a single file from the media service.
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
     * Deletes multiple files from the media service.
     * Errors are logged but not thrown to avoid blocking the main flow.
     *
     * @param fileUrls list of file URLs to delete
     */
    @Override
    public void deleteFiles(List<String> fileUrls) {
        if (fileUrls == null || fileUrls.isEmpty()) {
            return;
        }
        try {
            teraBoxWorkerClient.deleteFiles(fileUrls);
        } catch (Exception e) {
            log.error("Error deleting files: ", e);
        }
    }
}
