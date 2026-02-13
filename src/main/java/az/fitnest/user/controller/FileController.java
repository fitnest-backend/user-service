package az.fitnest.user.controller;

import az.fitnest.user.service.FileStorageService;
import az.fitnest.user.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "File Management", description = "Endpoints for uploading and managing files")
public class FileController {

    private final FileStorageService fileStorageService;

    @Operation(
            summary = "Upload a file",
            description = "Uploads a file to the storage service and returns the accessible URL. " +
                    "Supports images and other file types as configured."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "File uploaded successfully",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid file or unsupported file type",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            )
    })
    @PostMapping("/upload")
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<String>> uploadFile(
            @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file) {
        String url = fileStorageService.saveFile(file);
        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(url));
    }
}
