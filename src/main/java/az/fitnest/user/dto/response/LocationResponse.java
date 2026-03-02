package az.fitnest.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LocationResponse(
    Double lat,
    Double lng,
    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(type = "string", description = "Timestamp when the location was last updated", example = "15/01/2023 10:30:00", pattern = "^\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}$")
    LocalDateTime updatedAt
) {}
