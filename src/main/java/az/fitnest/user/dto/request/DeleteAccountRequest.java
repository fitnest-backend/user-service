package az.fitnest.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class DeleteAccountRequest {

    @JsonProperty("confirm")
    @AssertTrue(message = "Confirmation must be true")
    private Boolean confirm;

    private String reason;
}
