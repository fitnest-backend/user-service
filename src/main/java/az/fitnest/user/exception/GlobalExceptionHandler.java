package az.fitnest.user.exception;

import az.fitnest.user.dto.ApiError;
import az.fitnest.user.dto.ApiResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.grpc.StatusRuntimeException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException ex, HttpServletRequest request) {
        HttpStatus status = ex.getHttpStatus();
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(buildApiError(ex.getErrorCode(), ex.getMessage(), status, request.getRequestURI(), null)));
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

        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(buildApiError("VALIDATION_ERROR", "Validation failed", status, request.getRequestURI(), details)));
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

        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(buildApiError("VALIDATION_ERROR", message, status, request.getRequestURI(), details)));
    }

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleStatusRuntimeException(StatusRuntimeException ex, HttpServletRequest request) {
        String errorCode = "SERVICE_UNAVAILABLE";
        String statusDescription = ex.getStatus().getDescription();
        String errorMessage = (statusDescription != null && !statusDescription.isEmpty()) 
                ? "Identity service error: " + statusDescription 
                : "External service error";
        HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;

        switch (ex.getStatus().getCode()) {
            case UNAVAILABLE -> errorMessage = "Identity service is currently unavailable";
            case DEADLINE_EXCEEDED -> errorMessage = "Request to identity service timed out";
            case INVALID_ARGUMENT -> {
                errorCode = "INVALID_REQUEST";
                httpStatus = HttpStatus.BAD_REQUEST;
                errorMessage = "Invalid request to identity service: " + (statusDescription != null ? statusDescription : "");
            }
            case NOT_FOUND -> {
                errorCode = "RESOURCE_NOT_FOUND";
                httpStatus = HttpStatus.NOT_FOUND;
                errorMessage = "Resource not found in identity service: " + (statusDescription != null ? statusDescription : "");
            }
            case INTERNAL -> {
                errorMessage = "Identity service internal error: " + (statusDescription != null ? statusDescription : "");
            }
            case UNKNOWN -> {
                errorMessage = "Identity service encountered an unknown error: " + (statusDescription != null ? statusDescription : "");
            }
            default -> {
                if (statusDescription == null || statusDescription.isEmpty()) {
                    errorMessage = "Identity service encountered an error. Please try again later.";
                }
            }
        }

        return ResponseEntity
                .status(httpStatus)
                .body(ApiResponse.error(buildApiError(errorCode, errorMessage, httpStatus, request.getRequestURI(), null)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(buildApiError("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", status, request.getRequestURI(), null)));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(buildApiError("ACCESS_DENIED", "You do not have permission to access this resource", status, request.getRequestURI(), null)));
    }

    private ApiResponse<Void> wrap(
            String code,
            String message,
            HttpStatus status,
            String path,
            Map<String, Object> details
    ) {
        return ApiResponse.<Void>builder()
                .error(ApiError.builder()
                        .code(code)
                        .message(message)
                        .status(status.value())
                        .path(path)
                        .timestamp(OffsetDateTime.now())
                        .details(details)
                        .build())
                .build();
    }

    private ApiError buildApiError(String code, String message, HttpStatus status, String path, Object details) {
        return ApiError.builder()
                .code(code)
                .message(message)
                .status(status != null ? status.value() : null)
                .path(path)
                .timestamp(OffsetDateTime.now())
                .details(details)
                .build();
    }
}
