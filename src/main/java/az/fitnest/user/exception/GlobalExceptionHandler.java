package az.fitnest.user.exception;

import az.fitnest.user.dto.ApiResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.grpc.StatusRuntimeException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException ex, HttpServletRequest request) {
        log.warn("Business Exception: {} - {}", ex.getErrorCode(), ex.getMessage());
        HttpStatus status = ex.getHttpStatus();
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(buildError(ex.getErrorCode(), ex.getMessage(), status, request.getRequestURI(), null)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<Map<String, String>> fieldIssues = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "issue", error.getDefaultMessage()
                ))
                .toList();

        Map<String, Object> details = Map.of("fieldIssues", fieldIssues);
        HttpStatus status = HttpStatus.BAD_REQUEST;

        log.warn("Validation failed: {}", fieldIssues);
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(buildError("VALIDATION_ERROR", "Validation failed", status, request.getRequestURI(), details)));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String message = "Invalid request format";
        String detailText = "Invalid request body";

        Throwable cause = ex.getCause();
        if (cause instanceof JsonMappingException jme) {
            if (!jme.getPath().isEmpty()) {
                String field = jme.getPath().stream()
                        .map(JsonMappingException.Reference::getFieldName)
                        .collect(Collectors.joining("."));
                detailText = "Invalid value for field: " + field;
            } else {
                detailText = jme.getOriginalMessage();
            }
        } else if (cause != null) {
            detailText = cause.getMessage();
        }

        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> details = Map.of("message", detailText);

        log.warn("JSON parsing error: {}", detailText);
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(buildError("VALIDATION_ERROR", message, status, request.getRequestURI(), details)));
    }

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleStatusRuntimeException(StatusRuntimeException ex, HttpServletRequest request) {
        log.error("gRPC Service Error - Status Code: {}, Description: {}, Cause: {}",
                ex.getStatus().getCode(), ex.getStatus().getDescription(), ex.getCause());

        String errorCode = "SERVICE_UNAVAILABLE";
        String errorMessage = "External service error";
        HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;

        switch (ex.getStatus().getCode()) {
            case UNAVAILABLE -> errorMessage = "Identity service is currently unavailable";
            case DEADLINE_EXCEEDED -> errorMessage = "Request to identity service timed out";
            case INVALID_ARGUMENT -> {
                errorCode = "INVALID_REQUEST";
                httpStatus = HttpStatus.BAD_REQUEST;
                errorMessage = "Invalid request to identity service";
            }
            case NOT_FOUND -> {
                errorCode = "RESOURCE_NOT_FOUND";
                httpStatus = HttpStatus.NOT_FOUND;
                errorMessage = "Resource not found in identity service";
            }
            case INTERNAL -> errorMessage = "Identity service internal error";
            case UNKNOWN -> {
                errorMessage = "Identity service encountered an unknown error. Please try again later.";
                log.error("UNKNOWN gRPC error - this usually indicates a server-side issue", ex);
            }
            default -> {
                String description = ex.getStatus().getDescription();
                errorMessage = (description != null && !description.isEmpty())
                        ? "Identity service error: " + description
                        : "Identity service encountered an error. Please try again later.";
            }
        }

        return ResponseEntity
                .status(httpStatus)
                .body(ApiResponse.error(buildError(errorCode, errorMessage, httpStatus, request.getRequestURI(), null)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Internal Server Error: ", ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(buildError("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", status, request.getRequestURI(), null)));
    }

    private ApiResponse.ApiError buildError(String code, String message, HttpStatus status, String path, Object details) {
        return ApiResponse.ApiError.builder()
                .code(code)
                .message(message)
                .status(status != null ? status.value() : null)
                .path(path)
                .timestamp(OffsetDateTime.now())
                .details(details)
                .build();
    }
}
