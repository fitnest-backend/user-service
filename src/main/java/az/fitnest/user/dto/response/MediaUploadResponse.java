package az.fitnest.user.dto.response;

import lombok.Data;

@Data
public class MediaUploadResponse {
    private boolean success;
    private String message;
    private TeraBoxFileData data;
}
