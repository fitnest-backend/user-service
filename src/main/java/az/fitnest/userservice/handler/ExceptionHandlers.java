package az.fitnest.userservice.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import az.fitnest.userservice.exception.CustomException;
import az.fitnest.userservice.response.ExceptionResponse;
import az.fitnest.userservice.response.ValidationMessageResponse;

@RestControllerAdvice
public class ExceptionHandlers {
	
	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlers.class);
	
	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ExceptionResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
		logger.warn("HttpMessageNotReadableException: {}", exception.getMessage());

		ExceptionResponse error = new ExceptionResponse();
		error.setStatus(400);
		error.setMessage("Məlumat oxunmadı. Daxil edilən məlumatların dəqiqliyini yoxlayın.");
		error.setTimestamp(LocalDateTime.now());
		error.setInternalMessage(null);
		error.setSuccess(false);
		error.setType("HttpMessageNotReadableException");
		return error;
	}
	
	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ExceptionResponse handleOurRuntimeException(CustomException exception) {
		logger.warn("MyException: {}", exception.getInternalMessage());

		ExceptionResponse error = new ExceptionResponse();
		error.setStatus(exception.getStatus());
		error.setMessage(exception.getMessage());
		error.setTimestamp(LocalDateTime.now());
		error.setInternalMessage(null);
		error.setSuccess(false);
		error.setType(exception.getType());

		if (exception.getResult() != null) {
			BindingResult result = exception.getResult();
			List<FieldError> errors = result.getFieldErrors();
			List<ValidationMessageResponse> validations = new ArrayList<>();

			for (FieldError e : errors) {
				validations.add(new ValidationMessageResponse(e.getField(), e.getDefaultMessage()));
			}
			error.setValidations(validations);
		}

		return error;
	}
	
	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionResponse handleRuntimeException(RuntimeException ex) {
		logger.error("RuntimeException: {}", ex.getMessage(), ex); // stack trace ilə

		ExceptionResponse error = new ExceptionResponse();
		error.setStatus(500);
		error.setMessage("Server xətası");
		error.setInternalMessage(null);
		error.setTimestamp(LocalDateTime.now());
		error.setSuccess(false);
		error.setType("RUNTIME_EXCEPTION");
		return error;
	}

}
