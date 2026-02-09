package az.fitnest.user.profile.adapter.service;

import az.fitnest.user.profile.adapter.client.MediaClient;
import az.fitnest.user.profile.adapter.client.dto.media.MediaDeleteRequest;
import az.fitnest.user.profile.adapter.client.dto.media.MediaUploadResponse;
import az.fitnest.user.shared.exception.BadRequestException;
import az.fitnest.user.shared.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final MediaClient mediaClient;
    private static final String PROFILES_DIRECTORY = "/profiles";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png"
    );

    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateFile(file);

        try {
            ResponseEntity<MediaUploadResponse> responseEntity = mediaClient.uploadImage(file, PROFILES_DIRECTORY);
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

    private void validateFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Only JPEG and PNG images are allowed");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds maximum allowed size of 5MB");
        }
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return;
        }
        deleteFiles(List.of(fileUrl));
    }

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
