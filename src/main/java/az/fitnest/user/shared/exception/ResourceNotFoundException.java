package az.fitnest.user.shared.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {
    
    private static final long serialVersionUID = 1L;
    
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }
}
