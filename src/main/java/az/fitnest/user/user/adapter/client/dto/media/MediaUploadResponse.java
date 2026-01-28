package az.fitnest.user.user.adapter.client.dto.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaUploadResponse {
    private boolean success;
    private String message;
    private FileDetails data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileDetails {
        private String fs_id;
        private String path;
        private String server_filename;
        private Long size;
        private String md5;
        private Long category;
        private String isdir;
        private String thumbnailUrl;
        private Map<String, String> thumbnails;
    }
}
