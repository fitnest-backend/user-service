package az.fitnest.user.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false"
})
@AutoConfigureMockMvc
public class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private az.fitnest.user.client.StorageGrpcClient storageGrpcClient;

    @MockBean
    private az.fitnest.user.client.CachedIdentityGrpcClient cachedIdentityGrpcClient;

    @Test
    public void testUploadImage() throws Exception {
        // Mock the UserResponse if needed
        org.mockito.Mockito.when(cachedIdentityGrpcClient.getUserById(org.mockito.ArgumentMatchers.anyLong()))
                .thenReturn(az.fitnest.user.dto.response.IdentityUserResponse.builder()
                        .userId(1L).profileImageUrl("some_url").build());

        MockMultipartFile file = new MockMultipartFile(
                "image", "test.png", "image/png", "test data".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/me/profile-image")
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .header("X-User-Id", "1")
                        .header("X-Scopes", "ROLE_USER"))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
