package az.fitnest.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private String code;
    private String message;
    private Integer status;
    private String path;
    private OffsetDateTime timestamp;
    private Object details;
}
