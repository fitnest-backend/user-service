package az.fitnest.user.user.adapter.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateProfileImageRequest {

    @JsonProperty("image_url")
    private String imageUrl;
}
