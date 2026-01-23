package az.fitnest.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

public class ValidationException extends BaseException {
    
    private static final long serialVersionUID = 1L;
    
    private final BindingResult bindingResult;
    
    public ValidationException(String message, BindingResult bindingResult) {
        super(message, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        this.bindingResult = bindingResult;
    }
    
    public BindingResult getBindingResult() {
        return bindingResult;
    }
}
