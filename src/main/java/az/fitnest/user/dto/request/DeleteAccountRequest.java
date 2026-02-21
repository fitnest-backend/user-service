package az.fitnest.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;

public class DeleteAccountRequest {

    @JsonProperty("confirm")
    @AssertTrue(message = "Confirmation must be true")
    private Boolean confirm;

    private String reason;

    public Boolean getConfirm() {
        return confirm;
    }

    public void setConfirm(Boolean confirm) {
        this.confirm = confirm;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
