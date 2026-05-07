package az.fitnest.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockCustomerRequest {

    @NotBlank(message = "Reason cannot be empty")
    private String reason;

    private String note;
}