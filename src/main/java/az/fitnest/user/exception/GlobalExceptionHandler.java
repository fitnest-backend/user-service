package az.fitnest.user.exception;

import az.fitnest.user.dto.ApiResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException ex) {
        log.warn("Business Exception: {} - {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        log.warn("Validation failed: {}", details);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("VALIDATION_ERROR", details));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        String message = "Invalid request format";
        String details = "Invalid request body";

        // Try to extract more specific error information
        Throwable cause = ex.getCause();
        if (cause instanceof JsonMappingException jme) {
            // Extract the field path
            if (!jme.getPath().isEmpty()) {
                String field = jme.getPath().stream()
                        .map(ref -> ref.getFieldName())
                        .collect(Collectors.joining("."));
                details = "Invalid value for field: " + field;
            } else {
                details = jme.getOriginalMessage();
            }
        } else if (cause != null) {
            details = cause.getMessage();
        }

        log.warn("JSON parsing error: {}", details);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("VALIDATION_ERROR", details));
    }

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleStatusRuntimeException(StatusRuntimeException ex) {
        log.error("gRPC Service Error - Status: {}, Message: {}", ex.getStatus(), ex.getStatus().getDescription());

        String errorCode = "SERVICE_UNAVAILABLE";
        String errorMessage = "External service error";
        HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;

        switch (ex.getStatus().getCode()) {
            case UNAVAILABLE:
                errorMessage = "Identity service is currently unavailable";
                break;
            case DEADLINE_EXCEEDED:
                errorMessage = "Request to identity service timed out";
                break;
            case INVALID_ARGUMENT:
                errorCode = "INVALID_REQUEST";
                httpStatus = HttpStatus.BAD_REQUEST;
                errorMessage = "Invalid request to identity service";
                break;
            case NOT_FOUND:
                errorCode = "RESOURCE_NOT_FOUND";
                httpStatus = HttpStatus.NOT_FOUND;
                errorMessage = "Resource not found in identity service";
                break;
            case INTERNAL:
                errorMessage = "Identity service internal error";
                break;
            default:
                errorMessage = "Identity service error: " + ex.getStatus().getDescription();
        }

        return ResponseEntity
                .status(httpStatus)
                .body(ApiResponse.error(errorCode, errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Internal Server Error: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_SERVER_ERROR", "An unexpected error occurred."));
    }
}
