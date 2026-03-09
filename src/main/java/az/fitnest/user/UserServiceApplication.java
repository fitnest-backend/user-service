package az.fitnest.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableCaching
@EnableKafka
@EnableAsync
@Slf4j
public class UserServiceApplication {

    public static void main(String[] args) {
        try {
            SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
            log.info("Set SecurityContextHolder strategy to MODE_INHERITABLETHREADLOCAL before application start");
        } catch (Throwable t) {
            System.err.println("Warning: unable to set SecurityContextHolder strategy: " + t.getMessage());
        }
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
