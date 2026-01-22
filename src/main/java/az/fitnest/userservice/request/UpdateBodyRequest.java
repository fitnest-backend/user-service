package az.fitnest.userservice.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UpdateBodyRequest {
	
	private Long heightCm;
	
	private Long weightKg;
	
	private String gender;
	
	private LocalDate birthDate;
	
	

}
