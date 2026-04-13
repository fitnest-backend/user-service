package az.fitnest.user.service.impl;

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

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    private final az.fitnest.user.client.StorageGrpcClient storageGrpcClient;

    @Override
    public String saveFile(MultipartFile file) {
        return saveFile(file, "/uploads");
    }

    @Override
    public String saveFile(MultipartFile file, String directory) {
        return saveFile(file, directory, null);
    }

    @Override
    public String saveFile(MultipartFile file, String directory, String oldPath) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateFile(file);

        try {
            String extractedOldPath = extractIdFromUrl(oldPath);
            az.fitnest.user.dto.response.StorageFileData data = storageGrpcClient.uploadFile(file, directory, extractedOldPath);
            return String.valueOf(data.getFsId());
        } catch (az.fitnest.user.exception.InternalServerException | az.fitnest.user.exception.BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("error.file_upload_failed");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("error.file_empty");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("error.invalid_file_type");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("error.file_too_large");
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
            List<String> ids = fileUrls.stream()
                    .map(this::extractIdFromUrl)
                    .filter(id -> id != null && !id.isBlank())
                    .toList();
            if (!ids.isEmpty()) {
                storageGrpcClient.deleteFiles(ids);
            }
        } catch (Exception e) {
        }
    }

    private String extractIdFromUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }
        if (url.contains("/")) {
            String[] parts = url.split("/");
            return parts[parts.length - 1];
        }
        return url;
    }
}
