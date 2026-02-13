package az.fitnest.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private T data;
    private ApiError error;

    @JsonValue
    public Object asJson() {
        if (error != null) {
            return Map.of("error", error);
        }
        return data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(ApiError apiError) {
        return ApiResponse.<T>builder()
                .error(apiError)
                .build();
    }
}
