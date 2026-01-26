package az.fitnest.userservice.shared.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {
    
    private static final long serialVersionUID = 1L;
    
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }
}
