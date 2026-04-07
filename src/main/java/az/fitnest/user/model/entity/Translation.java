package az.fitnest.user.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "translations", indexes = {
        @Index(name = "idx_translations_entity", columnList = "entity_type, entity_id, language_code, field_name")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_translations_entity_field_lang", columnNames = {"entity_type", "entity_id", "field_name", "language_code"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Generic translation for entities")
public class Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false)
    @Schema(description = "Type of the entity", example = "GoalReference")
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    @Schema(description = "ID of the entity", example = "WEIGHT_LOSS")
    private String entityId;

    @Column(name = "language_code", nullable = false, length = 10)
    @Schema(description = "Language code", example = "EN")
    private String languageCode;

    @Column(name = "field_name", nullable = false)
    @Schema(description = "Name of the field", example = "title")
    private String fieldName;

    @Column(name = "field_value", columnDefinition = "TEXT")
    @Schema(description = "Translated value", example = "Lose Weight")
    private String fieldValue;
}
