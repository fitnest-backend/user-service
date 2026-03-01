package az.fitnest.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

public class ValidationException extends BaseException {

    private static final long serialVersionUID = 1L;

    private final BindingResult bindingResult;

    public ValidationException(String message, BindingResult bindingResult) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }
}
