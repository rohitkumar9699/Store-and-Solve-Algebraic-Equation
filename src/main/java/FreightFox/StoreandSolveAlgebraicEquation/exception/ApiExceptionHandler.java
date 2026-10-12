package FreightFox.StoreandSolveAlgebraicEquation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(EquationNotFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(EquationNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(exception.getMessage()));
	}

	@ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
	public ResponseEntity<ApiError> handleBadRequest(Exception exception) {
		String message = exception instanceof HttpMessageNotReadableException
				? "Request body is invalid or malformed." : exception.getMessage();
		return ResponseEntity.badRequest().body(new ApiError(message));
	}

	public record ApiError(String message) {
	}
}
