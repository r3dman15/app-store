package apple.appstore.exceptions;

import apple.appstore.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAppNotFound(AppNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("APP_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SearchFailedException.class)
    public ResponseEntity<ErrorResponse> handleSearchError(SearchFailedException e) {
        return ResponseEntity.status(503).body(new ErrorResponse("SEARCH_FAILED", e.getMessage()));
    }

    @ExceptionHandler(AppSaveException.class)
    public ResponseEntity<ErrorResponse> handleAppSaveException(AppSaveException e) {
        return ResponseEntity.status(503).body(new ErrorResponse("DB_SAVE_FAILED", e.getMessage()));
    }
}
