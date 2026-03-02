package az.fitnest.user.dto.response;


import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageFileData {
    private String path;
    private long size;
    private String md5;
    private long fsId;

}
