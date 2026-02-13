package az.fitnest.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

import az.fitnest.user.constants.EntityType;

@Entity
@Table(name = "favorites", indexes = {
    @Index(name = "idx_favorites_user_id", columnList = "user_id"),
    @Index(name = "idx_favorites_user_entity", columnList = "user_id, entity_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {
    
    /** Unique identifier for the favorite */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long favoriteId;
    
    /** ID of the user who favorited this item */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /** Type of entity being favorited (GYM, STORE, etc.) */
    @Column(name = "entity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityType entityType;
    
    /** ID of the favorited entity */
    @Column(name = "entity_id", nullable = false)
    private String entityId;

    /** Timestamp when the favorite was created */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
