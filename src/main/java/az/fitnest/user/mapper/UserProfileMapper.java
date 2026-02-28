package az.fitnest.user.mapper;

import az.fitnest.user.dto.response.*;
import az.fitnest.user.model.entity.*;
import java.time.*;

public final class UserProfileMapper {

    private UserProfileMapper() {}

    public static UserProfileResponse toUserProfileResponse(IdentityUserResponse userResponse, String profileImageUrl, String currentSubscription) {
        if (userResponse == null) return null;
        return UserProfileResponse.builder()
                .userId(userResponse.getUserId())
                .firstName(userResponse.getFirstName())
                .lastName(userResponse.getLastName())
                .mobile(userResponse.getMobile())
                .email(userResponse.getEmail())
                .profileImageUrl(profileImageUrl)
                .createdAt(parseCreatedAt(userResponse.getCreatedAt()))
                .currentSubscription(currentSubscription)
                .build();
    }

    public static GoalItemResponse toGoalItemResponse(GoalReference goal) {
        if (goal == null) return null;
        return GoalItemResponse.builder()
                .code(goal.getGoalCode())
                .title("") // Placeholder, as titles are in translations
                .subtitle("") // Placeholder, as subtitles are in translations
                .imageUrl(goal.getImageUrl())
                .build();
    }

    public static GoalResponse toGoalResponse(GoalReference reference, String goalCode, String title, String subtitle) {
        if (reference == null) return null;
        return GoalResponse.builder()
                .goalCode(goalCode)
                .title(title)
                .subtitle(subtitle)
                .imageUrl(reference.getImageUrl())
                .build();
    }

    private static LocalDateTime parseCreatedAt(String value) {
        if (value == null || value.isBlank()) return null;

        // 1) epoch millis
        try {
            if (value.chars().allMatch(Character::isDigit)) {
                long epochMillis = Long.parseLong(value);
                return Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
        } catch (Exception ignored) { }

        // 2) ISO local datetime
        try {
            return LocalDateTime.parse(value);
        } catch (java.time.format.DateTimeParseException ignored) { }

        // 3) RFC3339/ISO with offset -> preserve instant meaning
        try {
            return OffsetDateTime.parse(value).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (java.time.format.DateTimeParseException ignored) { }

        // 4) ISO instant (e.g., 2024-01-01T00:00:00Z)
        try {
            return Instant.parse(value).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (java.time.format.DateTimeParseException e) {
            return null;
        }
    }
}
