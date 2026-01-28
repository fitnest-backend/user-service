package az.fitnest.user.user.adapter.client;

import az.fitnest.user.user.adapter.client.dto.media.MediaDeleteRequest;
import az.fitnest.user.user.adapter.client.dto.media.MediaDeleteResponse;
import az.fitnest.user.user.adapter.client.dto.media.MediaUploadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "media-service", url = "${media.service.url:http://localhost:8086}")
public interface MediaClient {

    @PostMapping(value = "/api/v1/media/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<MediaUploadResponse> uploadImage(
            @RequestPart("image") MultipartFile file,
            @RequestParam(required = false) String directory
    );

    @DeleteMapping("/api/v1/media/files")
    ResponseEntity<MediaDeleteResponse> deleteFiles(
            @RequestBody MediaDeleteRequest request
    );
}
