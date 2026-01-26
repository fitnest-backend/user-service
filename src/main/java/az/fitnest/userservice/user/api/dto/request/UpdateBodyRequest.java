package az.fitnest.userservice.user.api.dto.request;

import java.time.LocalDate;

import az.fitnest.userservice.enums.Gender;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

@Data
public class UpdateBodyRequest {
	
	@NotNull
    @Min(100)
    @Max(250)
    private Integer heightCm;
	
    @NotNull
    @DecimalMin("30.0")
    @DecimalMax("300.0")
    private Double weightKg;
	
    @NotNull
	private Gender gender;
	
    @NotNull
    @Past
	private LocalDate birthDate;
	
	

}
