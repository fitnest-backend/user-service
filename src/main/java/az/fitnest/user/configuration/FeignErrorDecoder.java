package az.fitnest.user.configuration;

import az.fitnest.user.exception.BadRequestException;
import az.fitnest.user.exception.ResourceNotFoundException;
import az.fitnest.user.exception.InternalServerException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Feign error - method: {}, status: {}, reason: {}",
                methodKey, response.status(), response.reason());

        return switch (response.status()) {
            case 400 -> new BadRequestException("Bad request to downstream service");
            case 403 -> new az.fitnest.user.exception.ForbiddenException("Access denied to downstream service");
            case 404 -> new ResourceNotFoundException("Resource not found in downstream service");
            case 500, 502, 503, 504 -> new InternalServerException("Downstream service error: " + response.reason());
            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}

