package az.fitnest.user.user.adapter.service;

import az.fitnest.user.user.adapter.client.dto.media.MediaUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final MediaService mediaService;
    private static final String PROFILES_DIRECTORY = "/profiles";

    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateFile(file);

        MediaUploadResponse response = mediaService.uploadImage(file, PROFILES_DIRECTORY);

        if (response != null && response.isSuccess() && response.getData() != null) {
            String imageUrl = response.getData().getThumbnailUrl();
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = response.getData().getPath();
            }
            return imageUrl;
        } else {
            throw new RuntimeException("Upload failed for file: " + file.getOriginalFilename());
        }
    }

    private void validateFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        String[] allowedTypes = {"image/jpeg", "image/jpg", "image/png"};
        boolean isValidType = false;
        for (String allowedType : allowedTypes) {
            if (contentType.equals(allowedType)) {
                isValidType = true;
                break;
            }
        }

        if (!isValidType) {
            throw new IllegalArgumentException("Only JPEG and PNG images are allowed");
        }

        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 5MB");
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

        mediaService.deleteFiles(fileUrls);
    }
}
