package az.fitnest.userservice.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {
    
    private static final long serialVersionUID = 1L;
    
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN, "FORBIDDEN");
    }
}
