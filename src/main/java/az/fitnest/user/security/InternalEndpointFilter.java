package az.fitnest.user.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to protect internal endpoints from external access.
 * 
 * Internal endpoints (/api/v1/internal/**) require the X-Internal-Service header
 * to be present, indicating the request comes from another service in the cluster.
 */
@Slf4j
public class InternalEndpointFilter extends OncePerRequestFilter {

    private static final String INTERNAL_PATH_PREFIX = "/api/v1/internal";
    private static final String INTERNAL_SERVICE_HEADER = "X-Internal-Service";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Only check internal endpoints
        if (path.startsWith(INTERNAL_PATH_PREFIX)) {
            String internalHeader = request.getHeader(INTERNAL_SERVICE_HEADER);
            
            if (internalHeader == null || internalHeader.isBlank()) {
                log.warn("Blocked external access to internal endpoint: {} from {}", 
                         path, request.getRemoteAddr());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Internal endpoints are not accessible externally\"}");
                return;
            }
            
            log.debug("Internal request from service: {} to {}", internalHeader, path);
        }
        
        filterChain.doFilter(request, response);
    }
}
