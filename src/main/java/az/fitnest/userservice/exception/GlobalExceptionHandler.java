package az.fitnest.userservice.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import az.fitnest.userservice.dto.common.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ErrorResponse> handleBaseException(BaseException exception, WebRequest request) {
		logger.warn("BaseException [{}]: {}", exception.getErrorCode(), exception.getMessage());
		
		ErrorResponse.ErrorResponseBuilder builder = ErrorResponse.builder()
				.message(exception.getMessage())
				.code(exception.getErrorCode())
				.path(request.getDescription(false).replace("uri=", ""));
		
		if (exception instanceof ValidationException) {
			ValidationException validationException = (ValidationException) exception;
			BindingResult result = validationException.getBindingResult();
			if (result != null) {
				Map<String, Object> details = new HashMap<>();
				Map<String, String> validationErrors = new HashMap<>();
				for (FieldError error : result.getFieldErrors()) {
					validationErrors.put(error.getField(), error.getDefaultMessage());
				}
				details.put("validationErrors", validationErrors);
				builder.details(details);
			}
		}
		
		ErrorResponse errorResponse = builder.build();
		return ResponseEntity.status(exception.getHttpStatus()).body(errorResponse);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, WebRequest request) {
		logger.warn("MethodArgumentNotValidException: {}", exception.getMessage());
		
		BindingResult result = exception.getBindingResult();
		Map<String, Object> details = new HashMap<>();
		Map<String, String> validationErrors = new HashMap<>();
		
		for (FieldError error : result.getFieldErrors()) {
			validationErrors.put(error.getField(), error.getDefaultMessage());
		}
		details.put("validationErrors", validationErrors);
		
		ErrorResponse errorResponse = ErrorResponse.builder()
				.message("Validation failed")
				.code("VALIDATION_ERROR")
				.path(request.getDescription(false).replace("uri=", ""))
				.details(details)
				.build();
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception, WebRequest request) {
		logger.warn("HttpMessageNotReadableException: {}", exception.getMessage());

		ErrorResponse errorResponse = ErrorResponse.builder()
				.message("Məlumat oxunmadı. Daxil edilən məlumatların dəqiqliyini yoxlayın.")
				.code("HTTP_MESSAGE_NOT_READABLE")
				.path(request.getDescription(false).replace("uri=", ""))
				.build();
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
		logger.error("RuntimeException: {}", ex.getMessage(), ex);

		ErrorResponse errorResponse = ErrorResponse.builder()
				.message("Server xətası")
				.code("RUNTIME_EXCEPTION")
				.path(request.getDescription(false).replace("uri=", ""))
				.build();
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}

}
