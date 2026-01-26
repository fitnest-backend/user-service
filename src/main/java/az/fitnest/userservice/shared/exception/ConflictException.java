package az.fitnest.userservice.shared.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseException {
    
    private static final long serialVersionUID = 1L;
    
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT, "CONFLICT");
    }
}
