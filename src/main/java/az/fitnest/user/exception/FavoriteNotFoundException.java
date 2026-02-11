package az.fitnest.user.exception;

import org.springframework.http.HttpStatus;

public class FavoriteNotFoundException extends BaseException {

    private static final long serialVersionUID = 1L;

    public FavoriteNotFoundException(String message) {
        super(message, "FAVORITE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}

