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

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException ex, HttpServletRequest request) {
        HttpStatus status = ex.getHttpStatus();
        ApiError apiError = ApiError.builder()
                .code(ex.getErrorCode())
                .message(getLocalizedMessage(ex.getErrorCode(), ex.getMessage()))
                .status(status.value())
                .path(request.getRequestURI())
                .timestamp(OffsetDateTime.now())
                .build();
        return ResponseEntity.status(status).body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<Map<String, String>> fieldIssues = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "issue", safeMessage(error.getDefaultMessage())
                ))
                .toList();

        ApiError apiError = ApiError.builder()
                .code("VALIDATION_ERROR")
                .message(getMessage("error.validation"))
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .timestamp(OffsetDateTime.now())
                .details(Map.of("fieldIssues", fieldIssues))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError apiError = ApiError.builder()
                .code("BAD_REQUEST")
                .message(getMessage("error.invalid_json_format"))
                .status(status.value())
                .path(request.getRequestURI())
                .timestamp(OffsetDateTime.now())
                .build();
        return ResponseEntity.status(status).body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleStatusRuntimeException(StatusRuntimeException ex, HttpServletRequest request) {
        String errorCode = "SERVICE_UNAVAILABLE";
        String statusDescription = ex.getStatus().getDescription();
        String errorMessage = (statusDescription != null && !statusDescription.isEmpty())
                ? getMessage("error.identity_service_error", statusDescription)
                : getMessage("error.external_service_error");
        HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;

        switch (ex.getStatus().getCode()) {
            case UNAVAILABLE -> errorMessage = getMessage("error.service_unavailable");
            case DEADLINE_EXCEEDED -> errorMessage = getMessage("error.service_timeout");
            case INVALID_ARGUMENT -> {
                errorCode = "INVALID_REQUEST";
                httpStatus = HttpStatus.BAD_REQUEST;
                errorMessage = getMessage("error.service_invalid_request", (statusDescription != null ? statusDescription : ""));
            }
            case NOT_FOUND -> {
                errorCode = "RESOURCE_NOT_FOUND";
                httpStatus = HttpStatus.NOT_FOUND;
                errorMessage = getMessage("error.service_not_found", (statusDescription != null ? statusDescription : ""));
            }
            case INTERNAL -> {
                errorMessage = getMessage("error.service_internal_error", (statusDescription != null ? statusDescription : ""));
            }
            case UNKNOWN -> {
                errorMessage = getMessage("error.service_unknown", (statusDescription != null ? statusDescription : ""));
            }
            default -> {
                if (statusDescription == null || statusDescription.isEmpty()) {
                    errorMessage = getMessage("error.service_generic_error");
                }
            }
        }

        ApiError apiError = ApiError.builder()
                .code(errorCode)
                .message(errorMessage)
                .status(httpStatus.value())
                .path(request.getRequestURI())
                .timestamp(OffsetDateTime.now())
                .build();
        return ResponseEntity.status(httpStatus).body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex, HttpServletRequest request) {
        ApiError apiError = ApiError.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message(getMessage("error.unexpected"))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .timestamp(OffsetDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        ApiError apiError = ApiError.builder()
                .code("ACCESS_DENIED")
                .message(getMessage("error.access_denied"))
                .status(HttpStatus.FORBIDDEN.value())
                .path(request.getRequestURI())
                .timestamp(OffsetDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(apiError));
    }

    private String getLocalizedMessage(String errorCode, String defaultMessage) {
        String key = "error." + errorCode.toLowerCase();
        String message = getMessage(key);
        if (message.equals(key)) {
            // Try resolving by original errorCode
            message = getMessage(errorCode);
            if (message.equals(errorCode)) {
                return safeMessage(defaultMessage);
            }
        }
        return message;
    }

    private String safeMessage(String msg) {
        if (msg == null || msg.isBlank()) {
            return getMessage("error.unexpected");
        }
        // If the message looks like a key, try to resolve it
        if (msg.startsWith("error.")) {
            String resolved = getMessage(msg);
            if (!resolved.equals(msg)) {
                return resolved;
            }
        }
        return msg;
    }

    private String getMessage(String code) {
        return getMessage(code, null);
    }

    private String getMessage(String code, String arg) {
        try {
            return messageSource.getMessage(code, arg != null ? new Object[]{arg} : null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return code; // Fallback to code if message not found
        }
    }
}
