package nguyennhatquan.springbootreview.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import nguyennhatquan.springbootreview.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            if (error instanceof FieldError) {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            } else {
                errors.put(error.getObjectName(), error.getDefaultMessage());
            }
        });

        log.warn("Validation failed for request {}: {}", request.getDescription(false), errors);

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .code(400)
                .message("Validation Failed")
                .data(errors)
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, WebRequest request) {
        log.warn("Malformed JSON request: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.builder()
                .code(400)
                .message("Malformed JSON request")
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.warn("Type mismatch for parameter {}: {}", ex.getName(), ex.getMessage());

        ApiResponse<Object> response = ApiResponse.builder()
                .code(400)
                .message("Invalid parameter type for '" + ex.getName() + "'")
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, WebRequest request) {
        log.warn("Missing required parameter: {}", ex.getParameterName());

        ApiResponse<Object> response = ApiResponse.builder()
                .code(400)
                .message("Missing required parameter: " + ex.getParameterName())
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        log.warn("Authentication failed: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.builder()
                .code(401)
                .message("Authentication failed: Invalid credentials")
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.builder()
                .code(403)
                .message("Access denied: Insufficient permissions")
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.builder()
                .code(404)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoHandlerFoundException(
            NoHandlerFoundException ex, WebRequest request) {
        log.warn("Endpoint not found: {} {}", ex.getHttpMethod(), ex.getRequestURL());

        ApiResponse<Object> response = ApiResponse.builder()
                .code(404)
                .message("Endpoint not found")
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Unhandled exception occurred", ex);

        ApiResponse<Object> response = ApiResponse.builder()
                .code(500)
                .message("An unexpected error occurred. Please contact support.")
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidPaginationParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidPaginationParameterException(
            InvalidPaginationParameterException ex, WebRequest request) {
        log.warn("Invalid pagination parameter: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.builder()
                .code(400)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleEntityNotFoundException(
            EntityNotFoundException ex, WebRequest request) {
        log.warn("Entity not found: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.builder()
                .code(404)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(
            BadRequestException ex, WebRequest request) {
        log.warn("Bad Request: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.builder()
                .code(404)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<Object>> handleSecurityException(
            SecurityException ex, WebRequest request) {
        log.warn("Security exception: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.builder()
                .code(403)
                .message("Security error: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiResponse<Object>> handleRestClientException(
            org.springframework.web.client.RestClientException ex, WebRequest request) {
        log.error("Payment gateway error: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.builder()
                .code(503)
                .message("Payment gateway temporarily unavailable")
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        log.warn("Illegal argument: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.builder()
                .code(400)
                .message("Invalid argument: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private String extractPath(WebRequest request) {
        String description = request.getDescription(false);
        return description.replace("uri=", "");
    }
}
