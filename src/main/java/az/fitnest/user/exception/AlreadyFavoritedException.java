package az.fitnest.user.exception;

import org.springframework.http.HttpStatus;

public class AlreadyFavoritedException extends BaseException {

    private static final long serialVersionUID = 1L;

    public AlreadyFavoritedException(String message) {
        super(message, "ALREADY_FAVORITED", HttpStatus.CONFLICT);
    }
}

