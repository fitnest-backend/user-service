package az.fitnest.user.user.adapter.client;

import az.fitnest.user.user.adapter.client.dto.media.MediaDeleteRequest;
import az.fitnest.user.user.adapter.client.dto.media.MediaDeleteResponse;
import az.fitnest.user.user.adapter.client.dto.media.MediaUploadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Feign client for internal service-to-service communication with the Media Service.
 * 
 * <p>This client is used by the user-service to communicate with the media-service
 * for file upload and management operations. All endpoints are internal APIs and are 
 * not exposed via the API Gateway.</p>
 * 
 * <h2>Internal Endpoints Used:</h2>
 * <ul>
 *   <li>{@code POST /api/v1/internal/media/upload} - Upload an image file to media service</li>
 *   <li>{@code DELETE /api/v1/internal/media/files} - Delete files from media service</li>
 * </ul>
 * 
 * <p><strong>Target Service:</strong> media-service (Go service with internal media endpoints)</p>
 * 
 * @see az.fitnest.user.config.FeignConfig for authentication header forwarding configuration
 */
@FeignClient(
        name = "media-service", 
        url = "${media.service.url}",
        configuration = MediaClientConfig.class
)
public interface MediaClient {

    @PostMapping(value = "/api/v1/internal/media/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<MediaUploadResponse> uploadImage(
            @RequestPart("image") MultipartFile file,
            @RequestParam(required = false) String directory
    );

    @DeleteMapping("/api/v1/internal/media/files")
    ResponseEntity<MediaDeleteResponse> deleteFiles(
            @RequestBody MediaDeleteRequest request
    );
}
