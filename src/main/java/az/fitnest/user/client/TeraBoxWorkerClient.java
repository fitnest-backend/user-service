package az.fitnest.user.client;

import az.fitnest.user.dto.response.MediaUploadResponse;
import az.fitnest.user.dto.response.MediaDeleteResponse;
import az.fitnest.user.dto.response.DownloadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "terabox-worker-service", url = "${terabox.worker.url:http://terabox-worker-service:9090}", configuration = TeraBoxWorkerFeignConfig.class)
public interface TeraBoxWorkerClient {

    @PostMapping(value = "/api/v1/upload/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<MediaUploadResponse> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "directory", required = false) String directory
    );

    @DeleteMapping("/api/v1/upload/files")
    ResponseEntity<MediaDeleteResponse> deleteFiles(
            @RequestBody List<String> paths
    );

    @GetMapping("/api/v1/upload/download")
    ResponseEntity<DownloadResponse> downloadFile(@RequestParam("fileId") String fileId);
}
