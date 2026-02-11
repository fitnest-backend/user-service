package az.fitnest.user.client;

import az.fitnest.user.client.MediaDeleteRequest;
import az.fitnest.user.client.MediaDeleteResponse;
import az.fitnest.user.client.MediaUploadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
        name = "media-service", 
        url = "${media.service.url:http://media-service:8086}",
        configuration = MediaClientConfig.class
)
public interface MediaClient {

    @PostMapping(value = "/api/v1/internal/media/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<MediaUploadResponse> uploadImage(
            @RequestPart("image") MultipartFile file
    );

    @DeleteMapping("/api/v1/internal/media/files")
    ResponseEntity<MediaDeleteResponse> deleteFiles(
            @RequestBody MediaDeleteRequest request
    );
}
