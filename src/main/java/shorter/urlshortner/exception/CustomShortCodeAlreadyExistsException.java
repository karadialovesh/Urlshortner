package shorter.urlshortner.exception;

public class CustomShortCodeAlreadyExistsException extends RuntimeException {
    public CustomShortCodeAlreadyExistsException(String message) {
        super(message);
    }
}
