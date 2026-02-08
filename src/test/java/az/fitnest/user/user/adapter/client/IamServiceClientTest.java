package az.fitnest.user.user.adapter.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class IamServiceClientTest {

    private IamServiceClientConfig config;
    private RequestInterceptor interceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletRequestAttributes requestAttributes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        config = new IamServiceClientConfig();
        interceptor = config.internalServiceRequestInterceptor();
    }

    @Test
    void shouldAddInternalTokenAndRemoveAuthorization() {
        // Arrange
        RequestTemplate template = new RequestTemplate();
        template.header("Authorization", "Bearer some-user-token");

        // Mock current request context
        when(requestAttributes.getRequest()).thenReturn(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        // Act
        interceptor.apply(template);

        // Assert
        Map<String, Collection<String>> headers = template.headers();
        
        // 1. Verify Internal Token is present and correct
        assertTrue(headers.containsKey("X-Internal-Token"));
        assertEquals("fitnest-internal-token-2024-secure-v1", 
            headers.get("X-Internal-Token").iterator().next());
            
        // 2. Verify Authorization header is removed
        assertFalse(headers.containsKey("Authorization"));
    }

    @Test
    void shouldForwardUserContextHeaders() {
        // Arrange
        RequestTemplate template = new RequestTemplate();
        
        // Mock request headers
        when(request.getHeader("X-User-Id")).thenReturn("123");
        when(request.getHeader("X-User-Email")).thenReturn("user@example.com");
        when(request.getHeader("X-User-Roles")).thenReturn("ROLE_USER");
        when(request.getHeader("X-Request-ID")).thenReturn("req-123");

        when(requestAttributes.getRequest()).thenReturn(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        // Act
        interceptor.apply(template);

        // Assert
        Map<String, Collection<String>> headers = template.headers();
        
        assertEquals("123", headers.get("X-User-Id").iterator().next());
        assertEquals("user@example.com", headers.get("X-User-Email").iterator().next());
        assertEquals("ROLE_USER", headers.get("X-User-Roles").iterator().next());
        assertEquals("req-123", headers.get("X-Request-ID").iterator().next());
    }
}
