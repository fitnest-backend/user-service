package az.fitnest.user.dto.media;

import lombok.Data;
import java.util.List;

@Data
public class MediaDeleteRequest {
    private List<String> filePaths;
}
