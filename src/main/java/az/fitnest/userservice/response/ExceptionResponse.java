package az.fitnest.userservice.response;

import java.time.LocalDateTime;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Data;

@Data
public class ExceptionResponse {
	
	private String type;

	private Integer status;

	private Boolean success;

	private List<ValidationMessageResponse> validations;

	private String message;

	private String internalMessage;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime timestamp;

}
