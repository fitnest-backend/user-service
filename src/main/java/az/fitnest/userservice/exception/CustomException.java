package az.fitnest.userservice.exception;

import org.springframework.validation.BindingResult;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	@JsonProperty
	private String internalMessage;

	@JsonProperty
	private Integer status;

	@JsonProperty
	private String type;

	@JsonProperty
	private BindingResult result;

	public CustomException(String message, String internalMessage, String type, Integer status, BindingResult result) {
		super(message);
		this.internalMessage = internalMessage;
		this.type = type;
		this.status = status;
		this.result = result;

	}

}
