package az.fitnest.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
    
    @Id
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "language")
    private String language;
    
    @Column(name = "theme")
    private String theme;
    
    @Column(name = "notifications_push")
    private Boolean notificationsPush;
    
    @Column(name = "notifications_email")
    private Boolean notificationsEmail;
}
