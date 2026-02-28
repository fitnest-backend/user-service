import az.fitnest.user.model.entity.Language;
import az.fitnest.user.model.entity.GoalReference;
import az.fitnest.user.model.entity.Translation;
import az.fitnest.user.model.entity.UserLocation;
import az.fitnest.user.model.entity.UserProfile;
import az.fitnest.user.model.enums.Gender;
import az.fitnest.user.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final LanguageRepository languageRepository;
    private final GoalReferenceRepository goalReferenceRepository;
    private final TranslationRepository translationRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserLocationRepository userLocationRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            initLanguages();
            initGoals();
            initGoalTranslations();
            initGymTranslations();
            initMembershipPlanTranslations();
            initStoreTranslations();
            initTrainerTranslations();
            initGenderTranslations();
            initEntityTypeTranslations();
            initBmiMessageTranslations();
            initUserProfiles();
            initUserLocations();
        };
    }

    private void initLanguages() {
        createLanguageIfNotFound("AZ");
        createLanguageIfNotFound("EN");
        createLanguageIfNotFound("RU");
    }

    private void createLanguageIfNotFound(String code) {
        if (!languageRepository.existsByCode(code)) {
            Language language = new Language();
            language.setCode(code);
            languageRepository.save(language);
        }
    }

    private void initGoals() {
        createGoalIfNotFound("WEIGHT_LOSS");
        createGoalIfNotFound("MUSCLE_GAIN");
        createGoalIfNotFound("ENDURANCE");
        // Add more goals as needed
    }

    private void createGoalIfNotFound(String goalCode) {
        if (!goalReferenceRepository.existsById(goalCode) || goalReferenceRepository.findById(goalCode).get().getImageUrl() == null) {
            GoalReference goal = goalReferenceRepository.findById(goalCode).orElse(new GoalReference());
            goal.setGoalCode(goalCode);
            goal.setImageUrl("https://picsum.photos/seed/" + goalCode.toLowerCase() + "/400/300");
            goalReferenceRepository.save(goal);
        }
    }

    private void initGoalTranslations() {
        // WEIGHT_LOSS
        createTranslationIfNotFound("GoalReference", "WEIGHT_LOSS", "AZ", "title", "Çəki İtirmək");
        createTranslationIfNotFound("GoalReference", "WEIGHT_LOSS", "AZ", "subtitle", "Yağ yandırın və ideal çəkinizə çatıb");
        createTranslationIfNotFound("GoalReference", "WEIGHT_LOSS", "EN", "title", "Lose Weight");
        createTranslationIfNotFound("GoalReference", "WEIGHT_LOSS", "EN", "subtitle", "Burn fat and achieve your ideal weight");
        createTranslationIfNotFound("GoalReference", "WEIGHT_LOSS", "RU", "title", "Похудеть");
        createTranslationIfNotFound("GoalReference", "WEIGHT_LOSS", "RU", "subtitle", "Сжигайте жир и достигайте идеального веса");

        // MUSCLE_GAIN
        createTranslationIfNotFound("GoalReference", "MUSCLE_GAIN", "AZ", "title", "Əzələ Qazanmaq");
        createTranslationIfNotFound("GoalReference", "MUSCLE_GAIN", "AZ", "subtitle", "Güc və əzələ kütləsi qurun");
        createTranslationIfNotFound("GoalReference", "MUSCLE_GAIN", "EN", "title", "Gain Muscle");
        createTranslationIfNotFound("GoalReference", "MUSCLE_GAIN", "EN", "subtitle", "Build strength and muscle mass");
        createTranslationIfNotFound("GoalReference", "MUSCLE_GAIN", "RU", "title", "Набрать Мышцы");
        createTranslationIfNotFound("GoalReference", "MUSCLE_GAIN", "RU", "subtitle", "Развивайте силу и мышечную массу");

        // ENDURANCE
        createTranslationIfNotFound("GoalReference", "ENDURANCE", "AZ", "title", "Dayanıqlılığı Artırmaq");
        createTranslationIfNotFound("GoalReference", "ENDURANCE", "AZ", "subtitle", "Dözümlülüyü və ürək-damar sağlamlığını yaxşılaşdırın");
        createTranslationIfNotFound("GoalReference", "ENDURANCE", "EN", "title", "Improve Endurance");
        createTranslationIfNotFound("GoalReference", "ENDURANCE", "EN", "subtitle", "Enhance stamina and cardiovascular health");
        createTranslationIfNotFound("GoalReference", "ENDURANCE", "RU", "title", "Улучшить Выносливость");
        createTranslationIfNotFound("GoalReference", "ENDURANCE", "RU", "subtitle", "Повысьте выносливость и сердечно-сосудистое здоровье");
    }

    private void initGymTranslations() {
        // Assuming gym with ID "1"
        createTranslationIfNotFound("Gym", "1", "AZ", "name", "Premium Fitness Mərkəzi");
        createTranslationIfNotFound("Gym", "1", "AZ", "description", "Müasir avadanlıqlar və peşəkar məşqçilər ilə yüksək keyfiyyətli fitness mərkəzi");
        createTranslationIfNotFound("Gym", "1", "EN", "name", "Premium Fitness Center");
        createTranslationIfNotFound("Gym", "1", "EN", "description", "High-quality fitness center with modern equipment and professional trainers");
        createTranslationIfNotFound("Gym", "1", "RU", "name", "Премиум Фитнес Центр");
        createTranslationIfNotFound("Gym", "1", "RU", "description", "Высококачественный фитнес-центр с современным оборудованием и профессиональными тренерами");
    }

    private void initMembershipPlanTranslations() {
        // Assuming membership plan with ID "1"
        createTranslationIfNotFound("MembershipPlan", "1", "AZ", "name", "Əsas Abunəlik");
        createTranslationIfNotFound("MembershipPlan", "1", "EN", "name", "Basic Membership");
        createTranslationIfNotFound("MembershipPlan", "1", "RU", "name", "Базовое Членство");
    }

    private void initStoreTranslations() {
        // Assuming store with ID "1"
        createTranslationIfNotFound("Store", "1", "AZ", "name", "İdman Mağazası");
        createTranslationIfNotFound("Store", "1", "AZ", "description", "İdman avadanlıqları və geyimləri");
        createTranslationIfNotFound("Store", "1", "EN", "name", "Sports Store");
        createTranslationIfNotFound("Store", "1", "EN", "description", "Sports equipment and clothing");
        createTranslationIfNotFound("Store", "1", "RU", "name", "Спортивный Магазин");
        createTranslationIfNotFound("Store", "1", "RU", "description", "Спортивное оборудование и одежда");
    }

    private void initTrainerTranslations() {
        // Assuming trainer with ID "1"
        createTranslationIfNotFound("Trainer", "1", "AZ", "specialization", "Kardio və güc məşqləri");
        createTranslationIfNotFound("Trainer", "1", "EN", "specialization", "Cardio and strength training");
        createTranslationIfNotFound("Trainer", "1", "RU", "specialization", "Кардио и силовые тренировки");
    }

    private void initGenderTranslations() {
        // MALE
        createTranslationIfNotFound("Gender", "MALE", "AZ", "label", "Kişi");
        createTranslationIfNotFound("Gender", "MALE", "EN", "label", "Male");
        createTranslationIfNotFound("Gender", "MALE", "RU", "label", "Мужской");

        // FEMALE
        createTranslationIfNotFound("Gender", "FEMALE", "AZ", "label", "Qadın");
        createTranslationIfNotFound("Gender", "FEMALE", "EN", "label", "Female");
        createTranslationIfNotFound("Gender", "FEMALE", "RU", "label", "Женский");
    }

    private void initEntityTypeTranslations() {
        // GYM
        createTranslationIfNotFound("EntityType", "GYM", "AZ", "label", "İdman zalı");
        createTranslationIfNotFound("EntityType", "GYM", "EN", "label", "Gym");
        createTranslationIfNotFound("EntityType", "GYM", "RU", "label", "Тренажерный зал");

        // STORE
        createTranslationIfNotFound("EntityType", "STORE", "AZ", "label", "Mağaza");
        createTranslationIfNotFound("EntityType", "STORE", "EN", "label", "Store");
        createTranslationIfNotFound("EntityType", "STORE", "RU", "label", "Магазин");

        // TRAINER
        createTranslationIfNotFound("EntityType", "TRAINER", "AZ", "label", "Məşqçi");
        createTranslationIfNotFound("EntityType", "TRAINER", "EN", "label", "Trainer");
        createTranslationIfNotFound("EntityType", "TRAINER", "RU", "label", "Тренер");

        // PROGRAM
        createTranslationIfNotFound("EntityType", "PROGRAM", "AZ", "label", "Proqram");
        createTranslationIfNotFound("EntityType", "PROGRAM", "EN", "label", "Program");
        createTranslationIfNotFound("EntityType", "PROGRAM", "RU", "label", "Программа");
    }

    private void initBmiMessageTranslations() {
        // UNDERWEIGHT
        createTranslationIfNotFound("Message", "BmiMessage", "AZ", "UNDERWEIGHT", "Sizin çəkiniz normadan aşağıdır. Qidalanmanıza diqqət yetirin.");
        createTranslationIfNotFound("Message", "BmiMessage", "EN", "UNDERWEIGHT", "Your weight is below normal. Pay attention to your nutrition.");
        createTranslationIfNotFound("Message", "BmiMessage", "RU", "UNDERWEIGHT", "Ваш вес ниже нормы. Обратите внимание на питание.");

        // NORMAL
        createTranslationIfNotFound("Message", "BmiMessage", "AZ", "NORMAL", "Sizin çəkiniz normal diapazondadır. Belə davam edin!");
        createTranslationIfNotFound("Message", "BmiMessage", "EN", "NORMAL", "Your weight is in the normal range. Keep it up!");
        createTranslationIfNotFound("Message", "BmiMessage", "RU", "NORMAL", "Ваш вес в нормальном диапазоне. Продолжайте в том же духе!");

        // OVERWEIGHT
        createTranslationIfNotFound("Message", "BmiMessage", "AZ", "OVERWEIGHT", "Sizin çəkiniz normadan artıqdır. Aktivliyinizi artırın.");
        createTranslationIfNotFound("Message", "BmiMessage", "EN", "OVERWEIGHT", "Your weight is above normal. Increase your activity.");
        createTranslationIfNotFound("Message", "BmiMessage", "RU", "OVERWEIGHT", "Ваш вес выше нормы. Увеличьте активность.");

        // OBESE
        createTranslationIfNotFound("Message", "BmiMessage", "AZ", "OBESE", "Sizin çəkiniz piylənmə diapazonundadır. Mütəxəssislə məsləhətləşin.");
        createTranslationIfNotFound("Message", "BmiMessage", "EN", "OBESE", "Your weight is in the obesity range. Consult a specialist.");
        createTranslationIfNotFound("Message", "BmiMessage", "RU", "OBESE", "Ваш вес в диапазоне ожирения. Обратитесь к специалисту.");
    }

    private void initUserProfiles() {
        if (userProfileRepository.count() == 0) {
            // Admin user (ID 1)
            userProfileRepository.save(UserProfile.builder()
                    .userId(1L)
                    .heightCm(180.0)
                    .weightKg(85.0)
                    .gender(Gender.MALE)
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .goalCode("MUSCLE_GAIN")
                    .build());

            // Super Admin user (ID 2)
            userProfileRepository.save(UserProfile.builder()
                    .userId(2L)
                    .heightCm(175.0)
                    .weightKg(70.0)
                    .gender(Gender.MALE)
                    .birthDate(LocalDate.of(1985, 5, 15))
                    .goalCode("WEIGHT_LOSS")
                    .build());
        }
    }

    private void initUserLocations() {
        if (userLocationRepository.count() == 0) {
            // Admin user
            userLocationRepository.save(UserLocation.builder()
                    .userId(1L)
                    .lat(40.4093)
                    .lng(49.8671)
                    .updatedAt(LocalDateTime.now())
                    .build());

            // Super Admin user
            userLocationRepository.save(UserLocation.builder()
                    .userId(2L)
                    .lat(40.4095)
                    .lng(49.8675)
                    .updatedAt(LocalDateTime.now())
                    .build());
        }
    }

    private void createTranslationIfNotFound(String entityType, String entityId, String languageCode, String fieldName, String fieldValue) {
        if (!translationRepository.existsByEntityTypeAndEntityIdAndLanguageCodeAndFieldName(entityType, entityId, languageCode, fieldName)) {
            Translation translation = Translation.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .languageCode(languageCode)
                    .fieldName(fieldName)
                    .fieldValue(fieldValue)
                    .build();
            translationRepository.save(translation);
        }
    }
}
