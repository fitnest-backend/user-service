package az.fitnest.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String message,
        String code,
        LocalDateTime timestamp,
        String path,
        Map<String, Object> details
) {
    public static ErrorResponse of(String message, String code) {
        return new ErrorResponse(message, code, LocalDateTime.now(), null, null);
    }

    public static ErrorResponse of(String message, String code, String path) {
        return new ErrorResponse(message, code, LocalDateTime.now(), path, null);
    }

    public static ErrorResponse of(String message, String code, String path, Map<String, Object> details) {
        return new ErrorResponse(message, code, LocalDateTime.now(), path, details);
    }
}
