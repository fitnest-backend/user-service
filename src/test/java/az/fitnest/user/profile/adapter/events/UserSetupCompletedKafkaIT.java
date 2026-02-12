package az.fitnest.user.profile.adapter.events;
import az.fitnest.user.dto.UserSetupCompletedEvent;
import az.fitnest.user.repository.UserProfileRepository;
import az.fitnest.user.repository.FavoritesRepository;
import az.fitnest.user.repository.UserLocationRepository;
import az.fitnest.user.repository.GoalReferenceRepository;
import az.fitnest.user.service.EventIdempotencyService;
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
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
                "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.jpa.JpaAuditingAutoConfiguration," +
                "org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration",
        "spring.data.jpa.repositories.enabled=false",
        "app.warmup.enabled=false",
        "grpc.server.port=0"
})
@EmbeddedKafka(partitions = 1, topics = {"user-setup-completed"}, bootstrapServersProperty = "spring.kafka.bootstrap-servers")
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
    @MockBean
    private org.springframework.data.redis.connection.RedisConnectionFactory redisConnectionFactory;
    @MockBean
    private javax.sql.DataSource dataSource;
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
