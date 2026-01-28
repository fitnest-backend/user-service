package az.fitnest.user.user.adapter.client.dto.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaDeleteRequest {
    private List<String> filePaths;
}
