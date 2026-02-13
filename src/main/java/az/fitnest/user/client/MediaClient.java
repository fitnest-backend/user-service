package az.fitnest.user.client;

import az.fitnest.user.dto.media.MediaUploadResponse;
import az.fitnest.user.dto.media.MediaDeleteRequest;
import az.fitnest.user.dto.media.MediaDeleteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "media-service", url = "${media.service.url:http://media-service:8086}")
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
