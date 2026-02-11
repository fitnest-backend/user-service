package az.fitnest.user.service.impl;
import az.fitnest.user.service.*;

import az.fitnest.user.client.MediaClient;
import az.fitnest.user.client.MediaDeleteRequest;
import az.fitnest.user.client.MediaUploadResponse;
import az.fitnest.user.exception.BadRequestException;
import az.fitnest.user.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Arrays;

/**
 * Service for handling file storage operations.
 * Manages file uploads, validation, and deletion through the media service.
 *
 * <p>This service enforces file size limits and content type restrictions
 * to ensure only valid images are uploaded.
 *
 * @see MediaClient
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final MediaClient mediaClient;

    /** Maximum allowed file size: 5MB */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /** List of allowed image content types */
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png"
    );

    /**
     * Saves a file to the media service.
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
            ResponseEntity<MediaUploadResponse> responseEntity = mediaClient.uploadImage(file);
            MediaUploadResponse response = responseEntity.getBody();

            if (response != null && response.isSuccess() && response.getData() != null) {
                String imageUrl = response.getData().getThumbnailUrl();
                if (imageUrl == null || imageUrl.isEmpty()) {
                    imageUrl = response.getData().getPath();
                }
                return imageUrl;
            } else {
                log.error("Upload failed: {}", response != null ? response.getMessage() : "Unknown error");
                throw new BadRequestException("Failed to upload profile image");
            }
        } catch (Exception e) {
            log.error("Error calling media service: ", e);
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
            mediaClient.deleteFiles(new MediaDeleteRequest(fileUrls));
        } catch (Exception e) {
            log.warn("Failed to delete files: {}", fileUrls, e);
            // We don't throw exception here to avoid blocking main flow
        }
    }
}
