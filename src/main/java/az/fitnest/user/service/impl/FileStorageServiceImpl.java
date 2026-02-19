package az.fitnest.user.service.impl;


import az.fitnest.user.dto.response.MediaUploadResponse;
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

    private final az.fitnest.user.client.StorageGrpcClient storageGrpcClient;

    @Override
    public String saveFile(MultipartFile file) {
        return saveFile(file, "/uploads");
    }

    @Override
    public String saveFile(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateFile(file);

        try {
            az.fitnest.user.dto.response.StorageFileData data = storageGrpcClient.uploadFile(file, directory);
            return String.valueOf(data.getFs_id());
        } catch (az.fitnest.user.exception.InternalServerException | az.fitnest.user.exception.BadRequestException e) {
            throw e;
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
            storageGrpcClient.deleteFiles(fileUrls);
        } catch (Exception e) {
        }
    }
}
