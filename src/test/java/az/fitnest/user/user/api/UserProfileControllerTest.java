package az.fitnest.user.user.api;

import az.fitnest.user.security.FitnestSecurityFilter;
import az.fitnest.user.shared.exception.BadRequestException;
import az.fitnest.user.shared.exception.GlobalExceptionHandler;
import az.fitnest.user.user.adapter.service.UserProfileService;
import az.fitnest.user.user.api.dto.response.SetupResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import az.fitnest.user.shared.util.JwtUtil;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserProfileController userProfileController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userProfileController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilter(new FitnestSecurityFilter(jwtUtil))
                .build();
    }

    @Test
    void getSetupStatus_WithValidUserIdHeader_ShouldReturn200() throws Exception {
        // Arrange
        SetupResponse mockResponse = SetupResponse.builder()
                .setupRequired(true)
                .build();
        
        given(userProfileService.getSetupStatus()).willReturn(mockResponse);

        // Act & Assert
        // FitnestSecurityFilter should read X-User-Id and set Authentication
        // UserProfileService (mocked) is called.
        // NOTE: In this test, we mock the service, so we assume the service succeeds if called.
        // The detailed authentication context check happens if the service internally uses UserContext.
        // But since we mock the service, we verify that the request authenticates (filter doesn't block it?)
        // Actually, standalone setup with addFilter executes the filter.
        
        mockMvc.perform(get("/api/v1/me/setup")
                .header("X-User-Id", "123")
                .header("X-User-Email", "test@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.setup_required").value(true));
    }
/*
    @Test
    void getSetupStatus_WithoutUserId_ShouldReturn401() throws Exception {
        // In standalone setup, standard Spring Security is NOT active.
        // FitnestSecurityFilter will run, look for header, find null, and NOT set context.
        // However, there is no "authorizeHttpRequests().anyRequest().authenticated()" enforcement in standalone.
        // So this test might PASS (200 OK) with a mocked service if the controller doesn't block it.
        // BUT, UserProfileService implementation checks UserContext.
        // Since we MOCK userProfileService, it will return the stub value regardless of context!
        // So this test is NOT valid for standalone controller testing unless we throw from the mock.
    }
*/
    @Test
    void getSetupStatus_ServiceThrowsBadRequest_ShouldReturn400() throws Exception {
        // Simulate service throwing exception (e.g. from null check I added)
        given(userProfileService.getSetupStatus())
                .willThrow(new BadRequestException("User not authenticated or ID missing"));

        mockMvc.perform(get("/api/v1/me/setup")
                .header("X-User-Id", "123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("User not authenticated or ID missing"));
    }
    
    @Test
    void getSetupStatus_ServiceThrowsRuntimeException_ShouldReturn500WithDetails() throws Exception {
        // Simulate general failure (e.g. IamServiceClient fails)
        given(userProfileService.getSetupStatus())
                .willThrow(new RuntimeException("Connection refused"));

        mockMvc.perform(get("/api/v1/me/setup")
                .header("X-User-Id", "123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                // Verify my fix for error message is working
                .andExpect(jsonPath("$.error.message").value("Server xətası: Connection refused"));
    }
}
