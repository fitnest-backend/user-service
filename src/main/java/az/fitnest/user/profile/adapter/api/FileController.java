package az.fitnest.user.profile.adapter.api;

import az.fitnest.user.profile.adapter.service.FileStorageService;
import az.fitnest.user.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.saveFile(file);
        return ResponseEntity.ok(ApiResponse.success(url));
    }
}
