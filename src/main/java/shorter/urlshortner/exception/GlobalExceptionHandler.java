package shorter.urlshortner.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomShortCodeAlreadyExistsException.class)
    public ResponseEntity<String> handleCustomCodeInUse(CustomShortCodeAlreadyExistsException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(CustomshortcodeisreservedException.class)
    public ResponseEntity<String> handleCustomshortcodeisreserved(CustomshortcodeisreservedException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(400).body("Something went wrong: " + ex.getMessage());
    }
}
