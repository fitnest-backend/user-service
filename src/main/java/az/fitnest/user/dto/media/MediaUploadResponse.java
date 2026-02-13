package az.fitnest.user.dto.media;

import lombok.Data;

@Data
public class MediaUploadResponse {
    private boolean success;
    private String message;
    private TeraBoxFileData data;
}
