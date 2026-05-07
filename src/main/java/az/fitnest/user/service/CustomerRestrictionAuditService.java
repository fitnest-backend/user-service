package az.fitnest.user.service;

public interface CustomerRestrictionAuditService {
    void logRestrictedAttempt(Long userId, String action, String endpoint, String ipAddress);
}