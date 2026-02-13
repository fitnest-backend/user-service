package az.fitnest.user.service.impl;

import az.fitnest.user.client.TeraBoxWorkerClient;
import az.fitnest.user.dto.media.MediaUploadResponse;
import az.fitnest.user.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements az.fitnest.user.service.FileStorageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png"
    );

    private final TeraBoxWorkerClient teraBoxWorkerClient;

    @Override
    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateFile(file);

        try {
            ResponseEntity<MediaUploadResponse> responseEntity = teraBoxWorkerClient.uploadFile(file, "/uploads");

            MediaUploadResponse response = responseEntity.getBody();

            if (response != null && response.isSuccess() && response.getData() != null) {
                String imageUrl = response.getData().getPath();
                return imageUrl;
            } else {
                throw new BadRequestException("Failed to upload profile image");
            }
        } catch (Exception e) {
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

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return;
        }
        deleteFiles(List.of(fileUrl));
    }

    @Override
    public void deleteFiles(List<String> fileUrls) {
        if (fileUrls == null || fileUrls.isEmpty()) {
            return;
        }
        try {
            teraBoxWorkerClient.deleteFiles(fileUrls);
        } catch (Exception e) {
        }
    }
}
