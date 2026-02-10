package az.fitnest.user.favorites.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

import az.fitnest.user.favorites.domain.enums.EntityType;

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
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long favoriteId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "entity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityType entityType;
    
    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
   
}
