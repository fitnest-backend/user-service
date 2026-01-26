package az.fitnest.user.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String message;

    private String code;

    private LocalDateTime timestamp;

    private String path;

    private Map<String, Object> details;

    public static ErrorResponse of(String message, String code) {
        return ErrorResponse.builder()
                .message(message)
                .code(code)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse of(String message, String code, String path) {
        return ErrorResponse.builder()
                .message(message)
                .code(code)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse of(String message, String code, String path, Map<String, Object> details) {
        return ErrorResponse.builder()
                .message(message)
                .code(code)
                .path(path)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
