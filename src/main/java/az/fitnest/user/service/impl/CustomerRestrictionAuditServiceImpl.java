package az.fitnest.user.service.impl;

import az.fitnest.user.service.CustomerRestrictionAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerRestrictionAuditServiceImpl implements CustomerRestrictionAuditService {

    @Override
    public void logRestrictedAttempt(Long userId, String action, String endpoint, String ipAddress) {
        log.warn("[RESTRICTION AUDIT] userId={} | action={} | endpoint={} | ip={} | timestamp={}",
                userId, action, endpoint, ipAddress, LocalDateTime.now());
    }
}