package az.fitnest.user.dto.response;

public record BmiCalculatorResponse(
    double bmi,
    String category
) {}
