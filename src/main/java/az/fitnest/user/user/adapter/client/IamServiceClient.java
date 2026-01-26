package az.fitnest.user.user.adapter.client;

import az.fitnest.user.user.adapter.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "iam-service", url = "${iam.service.url:http://localhost:8080}")
public interface IamServiceClient {

    @GetMapping("/api/v1/internal/users/{userId}")
    UserResponse getUserById(@PathVariable("userId") Long userId);
}
