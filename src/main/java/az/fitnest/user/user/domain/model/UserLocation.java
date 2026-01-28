package az.fitnest.user.user.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLocation {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "lat", nullable = false)
    private Double lat;

    @Column(name = "lng", nullable = false)
    private Double lng;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

