package az.fitnest.user.dto.response;

import lombok.Data;

@Data
public class DownloadResponse {
    private boolean success;
    private String message;
    private String download_url;
}
