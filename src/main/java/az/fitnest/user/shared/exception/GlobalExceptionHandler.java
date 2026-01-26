package az.fitnest.user.shared.exception;

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

import az.fitnest.user.shared.dto.ErrorResponse;
import az.fitnest.user.shared.dto.ErrorWrapper;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ErrorWrapper> handleBaseException(BaseException exception, WebRequest request) {
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
		return ResponseEntity.status(exception.getHttpStatus()).body(ErrorWrapper.fromErrorResponse(errorResponse));
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorWrapper> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, WebRequest request) {
		logger.warn("MethodArgumentNotValidException: {}", exception.getMessage());
		
		BindingResult result = exception.getBindingResult();
		List<ErrorWrapper.FieldIssue> details = new ArrayList<>();
		
		for (FieldError error : result.getFieldErrors()) {
			details.add(ErrorWrapper.FieldIssue.builder()
					.field(error.getField())
					.issue(error.getDefaultMessage())
					.build());
		}
		
		ErrorWrapper errorWrapper = ErrorWrapper.builder()
				.error(ErrorWrapper.ErrorDetail.builder()
						.code("VALIDATION_ERROR")
						.message("Validation failed")
						.details(details)
						.build())
				.build();
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorWrapper);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorWrapper> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception, WebRequest request) {
		logger.warn("HttpMessageNotReadableException: {}", exception.getMessage());

		ErrorWrapper errorWrapper = ErrorWrapper.builder()
				.error(ErrorWrapper.ErrorDetail.builder()
						.code("HTTP_MESSAGE_NOT_READABLE")
						.message("Məlumat oxunmadı. Daxil edilən məlumatların dəqiqliyini yoxlayın.")
						.build())
				.build();
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorWrapper);
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorWrapper> handleRuntimeException(RuntimeException ex, WebRequest request) {
		logger.error("RuntimeException: {}", ex.getMessage(), ex);

		ErrorWrapper errorWrapper = ErrorWrapper.builder()
				.error(ErrorWrapper.ErrorDetail.builder()
						.code("RUNTIME_EXCEPTION")
						.message("Server xətası")
						.build())
				.build();
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorWrapper);
	}

}
