package az.fitnest.userservice.util;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class UserContextUtil {

	    private static final String USER_ID_HEADER = "X-User-Id";

	    public String getCurrentUserId() {
	        ServletRequestAttributes attributes =
	                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

	        if (attributes == null) {
	            throw new IllegalStateException("Request context not found");
	        }

	        String userId = attributes.getRequest().getHeader(USER_ID_HEADER);

	        if (userId == null || userId.isBlank()) {
	            throw new IllegalStateException("X-User-Id header is missing");
	        }

	        return userId;
	    }



}
