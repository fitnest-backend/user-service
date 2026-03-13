package az.fitnest.user.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends BaseException {
    
    private static final long serialVersionUID = 1L;
    
    public InternalServerException(String message) {
        super(message, "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
