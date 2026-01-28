package az.fitnest.user.shared.exception;

import org.springframework.http.HttpStatus;

public class AlreadyFavoritedException extends BaseException {

    private static final long serialVersionUID = 1L;

    public AlreadyFavoritedException(String message) {
        super(message, HttpStatus.CONFLICT, "ALREADY_FAVORITED");
    }
}

