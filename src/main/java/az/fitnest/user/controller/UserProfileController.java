package az.fitnest.user.controller;

import java.util.Map;

import az.fitnest.user.service.UserProfileService;
import az.fitnest.user.dto.*;
import az.fitnest.user.dto.request.*;
import az.fitnest.user.dto.response.*;
import az.fitnest.user.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import az.fitnest.user.client.CachedIdentityGrpcClient;
import az.fitnest.user.util.UserContext;
import az.fitnest.user.repository.TranslationRepository;
import az.fitnest.user.model.entity.Translation;
import org.springframework.security.access.prepost.PreAuthorize;
import az.fitnest.user.client.StorageGrpcClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "İstifadəçi profili, parametrləri və hesabını idarə etmək üçün ucluqlar")
public class UserProfileController {


    private final UserProfileService userProfileService;
    private final CachedIdentityGrpcClient cachedIdentityGrpcClient;
    private final TranslationRepository translationRepository;
    private final StorageGrpcClient storageGrpcClient;

    @Operation(summary = "İstifadəçi xülasəsini əldə edin", description = "İstifadəçinin profili və tərəqqisi haqqında qısa xülasə qaytarır.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Xülasə uğurla əldə edildi",
                    content = @Content(schema = @Schema(implementation = SummaryResponse.class), examples = @ExampleObject(value = "{\"totalWorkouts\": 25, \"totalCalories\": 1500}")))
    })
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<SummaryResponse>> getSummary() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getUserSummary()));
    }

    @Operation(summary = "Cari istifadəçi profilini əldə edin", description = "Autentifikasiya olunmuş istifadəçinin tam profil təfərrüatlarını qaytarır.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profil uğurla əldə edildi",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class), examples = @ExampleObject(value = "{\"user_id\": 1, \"first_name\": \"John\", \"last_name\": \"Doe\", \"email\": \"john.doe@example.com\", \"mobile\": \"+994500000000\", \"profile_image_url\": \"null\", \"current_subscription\": \"Bronze\"}")))
    })
    @GetMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMe() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getUserMe()));
    }

    @Operation(summary = "İstifadəçi profilini yeniləyin", description = "Autentifikasiya olunmuş istifadəçinin ad, soyad, e-poçt və mobil nömrəsini yeniləyir. Yalnız təqdim olunan sahələr yenilənəcək, digərləri dəyişməz qalacaq. Giriş məlumatları üzərində doğrulama aparılır.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profil uğurla yeniləndi",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class), examples = @ExampleObject(value = "{\"user_id\": 1, \"first_name\": \"John\", \"last_name\": \"Doe\", \"email\": \"john.doe@example.com\", \"mobile\": \"+994500000000\", \"profile_image_url\": \"null\", \"current_subscription\": \"Bronze\"}")))
    })
    @PutMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMe(@Valid @RequestBody UpdateUserProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.updateUserMe(request)));
    }

    @Operation(summary = "E-poçt dəyişmə sorğusu", description = "Yeni e-poçt ünvanına OTP kodu göndərir.")
    @PostMapping("/change-email/request")
    public ResponseEntity<ApiResponse<Map<String, Object>>> requestEmailChange(
            @RequestParam String newEmail,
            HttpServletRequest request) {
        userProfileService.requestEmailChange(newEmail);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "message", "OTP kodu yeni e-poçt ünvanınıza göndərildi",
                "status", 200,
                "path", request.getRequestURI(),
                "timestamp", java.time.OffsetDateTime.now()
        )));
    }

    @Operation(summary = "E-poçt dəyişməsini təsdiqləyin", description = "OTP kodu vasitəsilə yeni e-poçt ünvanını təsdiqləyir.")
    @PostMapping("/change-email/confirm")
    public ResponseEntity<ApiResponse<UserProfileResponse>> confirmEmailChange(@RequestParam String otpCode) {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.confirmEmailChange(otpCode)));
    }

    @Operation(summary = "Mobil nömrə dəyişmə sorğusu", description = "Yeni mobil nömrəyə OTP kodu göndərir.")
    @PostMapping("/change-mobile/request")
    public ResponseEntity<ApiResponse<Map<String, Object>>> requestMobileChange(
            @RequestParam String newMobile,
            HttpServletRequest request) {
        userProfileService.requestMobileChange(newMobile);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "message", "OTP kodu yeni mobil nömrənizə göndərildi",
                "status", 200,
                "path", request.getRequestURI(),
                "timestamp", java.time.OffsetDateTime.now()
        )));
    }

    @Operation(summary = "Mobil nömrə dəyişməsini təsdiqləyin", description = "OTP kodu vasitəsilə yeni mobil nömrəni təsdiqləyir.")
    @PostMapping("/change-mobile/confirm")
    public ResponseEntity<ApiResponse<UserProfileResponse>> confirmMobileChange(@RequestParam String otpCode) {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.confirmMobileChange(otpCode)));
    }

    @Operation(summary = "İstifadəçi məkanını yeniləyin", description = "İstifadəçinin cari şəhər və ölkəsini yeniləyir.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Məkan uğurla yeniləndi",
                    content = @Content(schema = @Schema(implementation = LocationResponse.class)))
    })
    @PutMapping("/location")
    public ResponseEntity<ApiResponse<LocationResponse>> updateLocation(@Valid @RequestBody UpdateLocationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.updateMyLocation(request)));
    }

    @Operation(summary = "Bədən göstəricilərini yeniləyin", description = "İstifadəçinin boy, çəki və s. kimi fiziki göstəricilərini yeniləyir.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bədən göstəriciləri uğurla yeniləndi", content = @Content(examples = @ExampleObject(value = "null")))
    })
    @PutMapping("/body")
    public ResponseEntity<ApiResponse<Void>> updateBody(@Valid @RequestBody UpdateBodyRequest request) {
        userProfileService.updateBody(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Bədən göstəricilərini əldə edin", description = "İstifadəçinin fiziki göstəricilərini qaytarır.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bədən göstəriciləri uğurla əldə edildi",
                    content = @Content(schema = @Schema(implementation = BodyInfoResponse.class)))
    })
    @GetMapping("/body")
    public ResponseEntity<ApiResponse<BodyInfoResponse>> getBody() {
        String userLanguage = getUserLanguage();
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getBodyInfo(userLanguage)));
    }

    @Operation(summary = "Profil şəklini yeniləyin", description = "İstifadəçi üçün yeni profil şəkli yükləyir və təyin edir.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
            schema = @Schema(implementation = ProfileImageUploadRequest.class)))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profil şəkli uğurla yeniləndi")
    })
    @PutMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateProfileImage(@RequestPart("image") MultipartFile file) {
        userProfileService.updateProfileImage(file);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "İstifadəçiyə təyin edilmiş hədəf arayışını əldə edin", description = "İstifadəçiyə təyin edilmiş sağlamlıq və fitnes hədəfini qaytarır.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Hədəf arayışı uğurla əldə edildi",
                    content = @Content(schema = @Schema(implementation = GoalResponse.class)))
    })
    @GetMapping("/reference/goals")
    public ResponseEntity<ApiResponse<GoalResponse>> getReferenceGoal() {
        String userLanguage = getUserLanguage();
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getGoal(userLanguage)));
    }

    @Operation(summary = "İstifadəçi hədəfini yeniləyin", description = "İstifadəçinin əsas fitnes və ya sağlamlıq hədəfini yeniləyir.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Hədəf uğurla yeniləndi", content = @Content(examples = @ExampleObject(value = "null")))
    })
    @PutMapping("/goal")
    public ResponseEntity<ApiResponse<Void>> updateGoal(@Valid @RequestBody UpdateGoalsRequest request) {
        userProfileService.updateGoal(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "İstifadəçi hədəfini əldə edin", description = "İstifadəçinin cari hədəfini qaytarır.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Hədəf uğurla əldə edildi",
                    content = @Content(schema = @Schema(implementation = GoalResponse.class)))
    })
    @GetMapping("/goal")
    public ResponseEntity<ApiResponse<GoalResponse>> getGoal() {
        String userLanguage = getUserLanguage();
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getGoal(userLanguage)));
    }

    @Operation(summary = "İstifadəçi seçimlərini yeniləyin", description = "Dil və mövzu kimi tətbiq parametrlərini yeniləyir.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Seçimlər uğurla yeniləndi", content = @Content(examples = @ExampleObject(value = "null")))
    })
    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<Void>> updatePreferences(@Valid @RequestBody UpdatePreferencesRequest request) {
        userProfileService.updatePreferences(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "İstifadəçi dilini yeniləyin", description = "İstifadəçinin üstünlük verdiyi dili yeniləyir.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dil uğurla yeniləndi", content = @Content(examples = @ExampleObject(value = "null")))
    })
    @PutMapping("/language")
    public ResponseEntity<ApiResponse<Void>> updateLanguage(@Valid @RequestBody UpdateLanguageRequest request) {
        userProfileService.updateLanguage(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Mövcud dilləri əldə edin", description = "Tətbiqdə mövcud olan bütün dilləri qaytarır.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dillər uğurla əldə edildi",
                    content = @Content(schema = @Schema(implementation = LanguageDto.class)))
    })
    @GetMapping("/languages")
    public ResponseEntity<ApiResponse<java.util.List<LanguageDto>>> getLanguages() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getAllLanguages()));
    }

    @Operation(summary = "Cari dili əldə edin", description = "İstifadəçinin seçdiyi cari dili qaytarır.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cari dil uğurla əldə edildi",
                    content = @Content(schema = @Schema(implementation = LanguageDto.class)))
    })
    @GetMapping("/language")
    public ResponseEntity<ApiResponse<LanguageDto>> getCurrentLanguage() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getCurrentLanguage()));
    }

    @Operation(summary = "Quraşdırma statusunu əldə edin", description = "İstifadəçi profilinin quraşdırılmasının cari tərəqqisini qaytarır.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quraşdırma statusu uğurla əldə edildi",
                    content = @Content(schema = @Schema(implementation = SetupResponse.class)))
    })
    @GetMapping("/setup")
    public ResponseEntity<ApiResponse<SetupResponse>> getSetup() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getSetupStatus()));
    }

    @Operation(summary = "İlkin profil quraşdırması", description = "İstifadəçi profilini tələb olunan ilkin detallarla quraşdırır.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profil quraşdırma mərhələsi uğurla tamamlandı",
                    content = @Content(schema = @Schema(implementation = SetupResponse.class)))
    })
    @PostMapping("/setup")
    public ResponseEntity<ApiResponse<SetupResponse>> setupProfile(@Valid @RequestBody SetupRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.setupProfile(request)));
    }

    @Operation(summary = "Profil quraşdırmasını tamamlayın", description = "İstifadəçi profilinin quraşdırılması prosesini yekunlaşdırır.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quraşdırma uğurla tamamlandı",
                    content = @Content(schema = @Schema(implementation = CompleteSetupResponse.class)))
    })
    @PostMapping("/setup/complete")
    public ResponseEntity<ApiResponse<CompleteSetupResponse>> completeSetup() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.completeSetup()));
    }

    @Operation(summary = "Profil quraşdırmasını keçin", description = "İstifadəçiyə profil quraşdırma prosesini keçməyə imkan verir.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quraşdırma uğurla keçildi",
                    content = @Content(schema = @Schema(implementation = CompleteSetupResponse.class)))
    })
    @PostMapping("/setup/skip")
    public ResponseEntity<ApiResponse<CompleteSetupResponse>> skipSetup() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.skipSetup()));
    }

    @Operation(summary = "Fitnes səviyyəsini əldə edin", description = "İstifadəçinin cari fitnes səviyyəsini qaytarır.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fitnes səviyyəsi uğurla əldə edildi",
                    content = @Content(schema = @Schema(implementation = FitnessLevelResponse.class)))
    })
    @GetMapping("/fitness-level")
    public ResponseEntity<ApiResponse<FitnessLevelResponse>> getFitnessLevel() {
        String userLanguage = getUserLanguage();
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getFitnessLevel(userLanguage)));
    }

    @Operation(summary = "Aktiv abunəliyi əldə edin", description = "İstifadəçinin aktiv abunəlik təfərrüatlarını qaytarır.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Abunəlik təfərrüatları uğurla əldə edildi",
                    content = @Content(schema = @Schema(implementation = ActiveSubscriptionResponse.class)))
    })
    @GetMapping("/subscription")
    public ResponseEntity<ApiResponse<ActiveSubscriptionResponse>> getActiveSubscription() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getActiveSubscription()));
    }

    @Operation(summary = "Hesabı silin", description = "İstifadəçinin hesabını və əlaqəli məlumatları silir.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Hesab uğurla silindi", content = @Content(examples = @ExampleObject(value = "null")))
    })
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@Valid @RequestBody DeleteAccountRequest request) {
        userProfileService.deleteAccount(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/profile/images/{fsId}")
    public ResponseEntity<StreamingResponseBody> streamProfileImage(@PathVariable String fsId) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")
                .body(outputStream -> {
                    storageGrpcClient.downloadFile(fsId, response -> {
                        if (response.hasFileData()) {
                            try {
                                outputStream.write(response.getFileData().toByteArray());
                            } catch (java.io.IOException e) {
                                throw new RuntimeException("Failed to stream file", e);
                            }
                        }
                    });
                    try {
                        outputStream.flush();
                    } catch (java.io.IOException e) {
                        // Ignore or log
                    }
                });
    }

    private String getUserLanguage() {
        Long userId = UserContext.getCurrentUserId();
        if (userId != null) {
            try {
                az.fitnest.user.dto.response.IdentityUserResponse user = cachedIdentityGrpcClient.getUserById(userId);
                String language = user.language();
                if (language != null && !language.isEmpty()) {
                    return language.toUpperCase();
                }
            } catch (Exception e) {
                // Log error or ignore
            }
        }
        return "AZ";
    }
}
