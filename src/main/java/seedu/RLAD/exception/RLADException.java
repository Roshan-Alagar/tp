package seedu.RLAD.exception;

public class RLADException extends RuntimeException {
    public RLADException(String message) {
        super(message);
    }

    public RLADException(String message, Throwable cause) {
        super(message, cause);
    }
}
