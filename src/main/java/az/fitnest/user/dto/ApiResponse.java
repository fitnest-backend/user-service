package az.fitnest.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    T data,
    ApiError error
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, null);
    }

    public static <T> ApiResponse<T> error(ApiError apiError) {
        return new ApiResponse<>(null, apiError);
    }

    @JsonValue
    public Object asJson() {
        if (error != null) {
            return Map.of("error", error);
        }
        return data != null ? data : Map.of();
    }
}
