package az.fitnest.user.mapper;

import az.fitnest.user.dto.response.*;
import az.fitnest.user.model.entity.*;

import java.time.*;

public final class UserProfileMapper {

    private UserProfileMapper() {
    }

    public static UserProfileResponse toUserProfileResponse(IdentityUserResponse userResponse, UserProfile profile, String profileImageUrl, String currentSubscription, String subscriptionStatus, Boolean notificationsEnabled) {
        if (userResponse == null || profile == null) return null;
        return UserProfileResponse.builder()
                .userId(userResponse.userId())
                .firstName(profile.getFirstName() != null ? profile.getFirstName() : "")
                .lastName(profile.getLastName() != null ? profile.getLastName() : "")
                .mobile(userResponse.mobile())
                .email(profile.getEmail() != null ? profile.getEmail() : "")
                .profileImageUrl(profileImageUrl)
                .currentSubscription(currentSubscription)
                .subscriptionStatus(subscriptionStatus)
                .notificationsEnabled(notificationsEnabled)
                .hasLocalPassword(userResponse.hasLocalPassword())
                .build();
    }

    public static UserProfileResponse toUserProfileResponse(IdentityUserResponse userResponse, UserProfile profile, String profileImageUrl, String currentSubscription, String subscriptionStatus) {
        return toUserProfileResponse(userResponse, profile, profileImageUrl, currentSubscription, subscriptionStatus, true);
    }

    public static GoalItemResponse toGoalItemResponse(GoalReference goal) {
        if (goal == null) return null;
        return GoalItemResponse.builder()
                .code(goal.getGoalCode())
                .title("")
                .subtitle("")
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

        try {
            if (value.chars().allMatch(Character::isDigit)) {
                long epochMillis = Long.parseLong(value);
                return Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
        } catch (Exception ignored) {
        }

        try {
            return LocalDateTime.parse(value);
        } catch (java.time.format.DateTimeParseException ignored) {
        }

        try {
            return OffsetDateTime.parse(value).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (java.time.format.DateTimeParseException ignored) {
        }

        try {
            return Instant.parse(value).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (java.time.format.DateTimeParseException e) {
            return null;
        }
    }
}
