package az.fitnest.user.shared.exception;

import org.springframework.http.HttpStatus;

public class FavoriteNotFoundException extends BaseException {

    private static final long serialVersionUID = 1L;

    public FavoriteNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "FAVORITE_NOT_FOUND");
    }
}

