package az.fitnest.user.controller;

import az.fitnest.user.dto.ApiResponse;
import az.fitnest.user.dto.response.ErrorResponse;
import az.fitnest.user.dto.response.UserProfileV2Response;
import az.fitnest.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/me")
@RequiredArgsConstructor
@Tag(name = "User Profile V2", description = "İstifadəçi profili (Coin balansı daxil olmaqla)")
public class UserProfileV2Controller {

    private final UserProfileService userProfileService;

    @Operation(
            summary = "Cari istifadəçi profilini əldə edin (v2)",
            description = "Profil məlumatları ilə birlikdə Coin balansı, AZN ekvivalenti (admin spend rate qaydasına əsasən) və etibarlılıq tarixini qaytarır."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Profil uğurla əldə edildi",
                    content = @Content(
                            schema = @Schema(implementation = UserProfileV2Response.class),
                            examples = @ExampleObject(value = "{\"user_id\":1,\"first_name\":\"John\",\"last_name\":\"Doe\",\"email\":\"john.doe@example.com\",\"mobile\":\"+994500000000\",\"profile_image_url\":null,\"current_subscription\":\"Bronze\",\"subscription_status\":\"ACTIVE\",\"coin_balance\":320.00,\"coin_azn_equivalent\":32.00,\"coin_validity_date\":\"2027-09-01T12:00:00\"}")
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Profil tapılmadı",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<UserProfileV2Response>> getMeV2() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getUserMeV2()));
    }
}
