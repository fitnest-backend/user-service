package az.fitnest.user.user.adapter.client.dto.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaDeleteResponse {
    private boolean success;
    private String message;
    private Object data;
}
