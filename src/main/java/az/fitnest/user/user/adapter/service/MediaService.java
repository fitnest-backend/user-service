package az.fitnest.user.user.adapter.service;

import az.fitnest.user.user.adapter.client.MediaClient;
import az.fitnest.user.user.adapter.client.dto.media.MediaDeleteRequest;
import az.fitnest.user.user.adapter.client.dto.media.MediaDeleteResponse;
import az.fitnest.user.user.adapter.client.dto.media.MediaUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaClient mediaClient;
    private static final String PROFILES_DIRECTORY = "/profiles";

    public MediaUploadResponse uploadImage(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be null or empty");
        }

        try {
            String targetDirectory = (directory != null && !directory.isBlank()) ? directory : PROFILES_DIRECTORY;
            ResponseEntity<MediaUploadResponse> response = mediaClient.uploadImage(file, targetDirectory);
            return java.util.Objects.requireNonNull(response.getBody(), "Upload response body is null");
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    public MediaDeleteResponse deleteFiles(List<String> filePaths) {
        if (filePaths == null || filePaths.isEmpty()) {
            throw new IllegalArgumentException("File paths must not be null or empty");
        }

        try {
            MediaDeleteRequest request = new MediaDeleteRequest(filePaths);
            ResponseEntity<MediaDeleteResponse> response = mediaClient.deleteFiles(request);
            return java.util.Objects.requireNonNull(response.getBody(), "Delete response body is null");
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to delete files", e);
        }
    }
}
