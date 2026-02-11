package az.fitnest.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorWrapper {

    @JsonProperty("error")
    private ErrorDetail error;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetail {
        private String code;
        private String message;
        private List<FieldIssue> details;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldIssue {
        private String field;
        private String issue;
    }

    public static ErrorWrapper fromErrorResponse(ErrorResponse errorResponse) {
        List<FieldIssue> details = null;
        if (errorResponse.getDetails() != null && errorResponse.getDetails().containsKey("validationErrors")) {
            @SuppressWarnings("unchecked")
            Map<String, String> validationErrors = (Map<String, String>) errorResponse.getDetails().get("validationErrors");
            if (validationErrors != null) {
                details = validationErrors.entrySet().stream()
                        .map(entry -> FieldIssue.builder()
                                .field(entry.getKey())
                                .issue(entry.getValue())
                                .build())
                        .toList();
            }
        }

        return ErrorWrapper.builder()
                .error(ErrorDetail.builder()
                        .code(errorResponse.getCode())
                        .message(errorResponse.getMessage())
                        .details(details)
                        .build())
                .build();
    }
}
