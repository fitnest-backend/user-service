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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionSystemException;
import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;

import az.fitnest.user.shared.dto.ErrorResponse;
import az.fitnest.user.shared.dto.ErrorWrapper;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ErrorWrapper> handleBaseException(BaseException exception, HttpServletRequest request) {
		logger.warn("BaseException [{}]: {}", exception.getErrorCode(), exception.getMessage());
		
		ErrorResponse.ErrorResponseBuilder builder = ErrorResponse.builder()
				.message(exception.getMessage())
				.code(exception.getErrorCode())
				.path(request.getRequestURI());
		
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
	public ResponseEntity<ErrorWrapper> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception, HttpServletRequest request) {
		logger.warn("HttpMessageNotReadableException: {}", exception.getMessage());

		ErrorWrapper errorWrapper = ErrorWrapper.builder()
				.error(ErrorWrapper.ErrorDetail.builder()
						.code("HTTP_MESSAGE_NOT_READABLE")
						.message("Məlumat oxunmadı. Daxil edilən məlumatların dəqiqliyini yoxlayın.")
						.build())
				.build();
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorWrapper);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorWrapper> handleDataIntegrityViolation(
			DataIntegrityViolationException exception,
			HttpServletRequest request
	) {
		logger.warn("Data integrity violation at {}: {}", request.getRequestURI(), exception.getMessage());
		
		String message = "Məlumat bazası xətası. Daxil edilən məlumatların unikallığını və ya tamlığını yoxlayın.";
		String code = "DATA_INTEGRITY_VIOLATION";
		
		if (exception.getMessage() != null && exception.getMessage().contains("uk_users_mobile")) {
			message = "Bu mobil nömrə artıq qeydiyyatdan keçib.";
			code = "DUPLICATE_MOBILE";
		} else if (exception.getMessage() != null && exception.getMessage().contains("uk_users_email")) {
			message = "Bu email artıq qeydiyyatdan keçib.";
			code = "DUPLICATE_EMAIL";
		}

		ErrorWrapper errorWrapper = ErrorWrapper.builder()
				.error(ErrorWrapper.ErrorDetail.builder()
						.code(code)
						.message(message)
						.build())
				.build();

		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorWrapper);
	}

	@ExceptionHandler(TransactionSystemException.class)
	public ResponseEntity<ErrorWrapper> handleTransactionSystemException(
			TransactionSystemException exception,
			HttpServletRequest request
	) {
		logger.error("Transaction system exception at {}: {}", request.getRequestURI(), exception.getMessage());
		
		Throwable cause = exception.getRootCause();
		if (cause instanceof ConstraintViolationException) {
			return handleConstraintViolationException((ConstraintViolationException) cause, request);
		}

		ErrorWrapper errorWrapper = ErrorWrapper.builder()
				.error(ErrorWrapper.ErrorDetail.builder()
						.code("TRANSACTION_ERROR")
						.message("Əməliyyat zamanı xəta baş verdi.")
						.build())
				.build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorWrapper);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorWrapper> handleConstraintViolationException(
			ConstraintViolationException exception,
			HttpServletRequest request
	) {
		List<ErrorWrapper.FieldIssue> details = new ArrayList<>();
		exception.getConstraintViolations().forEach(violation -> {
			details.add(ErrorWrapper.FieldIssue.builder()
					.field(violation.getPropertyPath().toString())
					.issue(violation.getMessage())
					.build());
		});

		ErrorWrapper errorWrapper = ErrorWrapper.builder()
				.error(ErrorWrapper.ErrorDetail.builder()
						.code("CONSTRAINT_VIOLATION")
						.message("Məlumat doğruluğu xətası")
						.details(details)
						.build())
				.build();

		return ResponseEntity.badRequest().body(errorWrapper);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorWrapper> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
		logger.error("RuntimeException: {}", ex.getMessage(), ex);

		ErrorWrapper errorWrapper = ErrorWrapper.builder()
				.error(ErrorWrapper.ErrorDetail.builder()
						.code("RUNTIME_EXCEPTION")
				.message("Server xətası: " + ex.getMessage())
						.build())
				.build();
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorWrapper);
	}

}
