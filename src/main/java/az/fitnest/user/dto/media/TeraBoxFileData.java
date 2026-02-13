package az.fitnest.user.dto.media;

import lombok.Data;

@Data
public class TeraBoxFileData {
    private String path;
    private long size;
    private String md5;
}
