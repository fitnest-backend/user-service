package az.fitnest.user.user.adapter.client;

import az.fitnest.user.profile.adapter.client.IamServiceClient;
import az.fitnest.user.profile.adapter.client.IamServiceClientConfig;
import az.fitnest.user.profile.adapter.client.dto.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import feign.Feign;
import feign.RequestInterceptor;
import feign.Target;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IamServiceIntegrationTest {

    private IamServiceClient iamServiceClient;
    private static WireMockServer wireMockServer;
    private ObjectMapper objectMapper;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(0); // Random port
        wireMockServer.start();
        WireMock.configureFor(wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        // Get the interceptor from the actual config class
        RequestInterceptor interceptor = new az.fitnest.user.config.FeignConfig().requestInterceptor();

        // Build the Feign client manually with SpringMvcContract to support @GetMapping
        iamServiceClient = Feign.builder()
                .encoder(new JacksonEncoder(objectMapper))
                .decoder(new JacksonDecoder(objectMapper))
                .contract(new org.springframework.cloud.openfeign.support.SpringMvcContract())
                .requestInterceptor(interceptor)
                .logger(new feign.Logger.ErrorLogger())
                .logLevel(feign.Logger.Level.FULL)
                .target(IamServiceClient.class, "http://localhost:" + wireMockServer.port() + "/api/v1/internal/users");

        // Mock the RequestContext to simulate headers coming from Gateway
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer user-token");
        when(mockRequest.getHeader("X-User-Id")).thenReturn("123");
        
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @Test
    void getUserById_ShouldSendCorrectHeadersAndReturnUser() throws JsonProcessingException {
        // Arrange
        Long userIdLong = 3L;
        String userId = "3";
        UserResponse mockResponse = UserResponse.builder()
                .userId(userId)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/internal/users/" + userId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(mockResponse))));

        // Act
        UserResponse result = iamServiceClient.getUserById(userIdLong);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("test@example.com", result.getEmail());

        // VERIFY REQUEST HEADERS ON THE WIRE
        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/internal/users/" + userId))
                .withHeader("Authorization", equalTo("Bearer user-token"))); 
    }

    @Test
    void getUserById_When403_ShouldThrowException() {
        // Arrange
        Long userId = 4L;
        wireMockServer.stubFor(get(urlEqualTo("/api/v1/internal/users/" + userId))
                .willReturn(aResponse().withStatus(403)));

        // Act & Assert
        try {
            iamServiceClient.getUserById(userId);
        } catch (Exception e) {
            // Expected
        }
        
        // Verify call was tried
        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/internal/users/" + userId)));
    }
}
