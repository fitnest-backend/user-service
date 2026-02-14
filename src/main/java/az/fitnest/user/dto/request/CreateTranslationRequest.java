package az.fitnest.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create or update a translation")
public class CreateTranslationRequest {

    @Schema(description = "Type of the entity", example = "Gender")
    private String entityType;

    @Schema(description = "ID of the entity", example = "MALE")
    private String entityId;

    @Schema(description = "Language code", example = "AZ")
    private String languageCode;

    @Schema(description = "Name of the field", example = "label")
    private String fieldName;

    @Schema(description = "Translated value", example = "Kişi")
    private String fieldValue;
}
