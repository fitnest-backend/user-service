package az.fitnest.user.dto.response;


import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DownloadResponse {
    private boolean success;
    private String message;
    private String download_url;

}
