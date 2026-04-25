package az.fitnest.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.ALWAYS)
@Schema(description = "Admin üçün istifadəçi məlumatları")
public record AdminUserResponse(
    @Schema(description = "İstifadəçi ID-si", example = "123")
    Long id,

    @Schema(description = "Tam Ad (Ad + Soyad)", example = "Kamal Əliyev")
    String fullName,

    @Schema(description = "Mobil nömrə", example = "0501234567")
    String phoneNumber,

    @Schema(description = "Email", example = "kamal@fitnest.az")
    String email,

    @Schema(description = "İstifadəçi statusu (ACTIVE, INACTIVE, LOCKED, NO_SESSIONS)", example = "ACTIVE")
    String userStatus,

    @Schema(description = "Abunə statusu (aktiv, dondurulmuş və s.)", example = "aktiv")
    String subscriptionStatus
) {}
