package az.fitnest.user.dto.response;

import lombok.Data;

@Data
public class StorageFileData {
    private String path;
    private long size;
    private String md5;
    private long fsId;
}
