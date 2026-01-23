package az.fitnest.userservice.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception class for all application-specific exceptions.
 * Following 2025 Spring Boot best practices for centralized exception handling.
 */
public abstract class BaseException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private final HttpStatus httpStatus;
    private final String errorCode;
    
    protected BaseException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
    
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
