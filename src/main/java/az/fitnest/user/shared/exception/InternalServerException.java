package az.fitnest.user.shared.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends BaseException {
    
    private static final long serialVersionUID = 1L;
    
    public InternalServerException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");
    }
}
