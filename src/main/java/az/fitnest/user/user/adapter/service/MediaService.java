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
        try {
            String targetDirectory = directory != null ? directory : PROFILES_DIRECTORY;
            ResponseEntity<MediaUploadResponse> response = mediaClient.uploadImage(file, targetDirectory);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        }
    }

    public MediaDeleteResponse deleteFiles(List<String> filePaths) {
        try {
            MediaDeleteRequest request = MediaDeleteRequest.builder()
                    .filePaths(filePaths)
                    .build();
            ResponseEntity<MediaDeleteResponse> response = mediaClient.deleteFiles(request);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete files: " + e.getMessage(), e);
        }
    }
}
