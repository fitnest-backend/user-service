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
    void shouldForwardAuthorizationHeaderAndNotAddInternalToken() {
        // Arrange
        RequestTemplate template = new RequestTemplate();
        String jwt = "Bearer some-user-token";
        
        // Mock current request context
        when(request.getHeader("Authorization")).thenReturn(jwt);
        when(requestAttributes.getRequest()).thenReturn(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        // Act
        interceptor.apply(template);

        // Assert
        Map<String, Collection<String>> headers = template.headers();
        
        // 1. Verify Internal Token is NOT present
        assertFalse(headers.containsKey("X-Internal-Token"));
            
        // 2. Verify Authorization header is forwarded
        assertTrue(headers.containsKey("Authorization"));
        assertEquals(jwt, headers.get("Authorization").iterator().next());
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
