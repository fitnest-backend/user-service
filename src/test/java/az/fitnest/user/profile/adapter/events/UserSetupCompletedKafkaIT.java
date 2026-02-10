package az.fitnest.user.profile.adapter.events;
import az.fitnest.identity.user.events.UserSetupCompletedEvent;
import az.fitnest.user.profile.adapter.persistence.UserProfileRepository;
import az.fitnest.user.favorites.adapter.persistence.FavoritesRepository;
import az.fitnest.user.profile.adapter.persistence.UserLocationRepository;
import az.fitnest.user.profile.adapter.persistence.GoalReferenceRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import java.time.Duration;
import java.util.UUID;
import static org.mockito.Mockito.*;
@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.data.jpa.repository.config.JpaRepositoriesAutoConfiguration",
        "app.warmup.enabled=false"
})
@EmbeddedKafka(partitions = 1, topics = {"user-setup-completed"})
class UserSetupCompletedKafkaIT {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @MockBean
    private UserProfileRepository userProfileRepository;
    @MockBean
    private EventIdempotencyService idempotencyService;
    @MockBean
    private FavoritesRepository favoritesRepository;
    @MockBean
    private UserLocationRepository userLocationRepository;
    @MockBean
    private GoalReferenceRepository goalReferenceRepository;
    @Test
    void consumesEventAndSavesProfile() {
        when(idempotencyService.markProcessedIfNew(anyString())).thenReturn(true);
        when(userProfileRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        UserSetupCompletedEvent event = UserSetupCompletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .userId(1L)
                .timestamp(System.currentTimeMillis())
                .source("identity-service")
                .build();
        kafkaTemplate.send("user-setup-completed", "1", event);
        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                verify(userProfileRepository, atLeastOnce()).save(any())
        );
    }
}
