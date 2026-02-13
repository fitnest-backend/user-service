package az.fitnest.user.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class MediaDeleteRequest {
    private List<String> filePaths;
}
