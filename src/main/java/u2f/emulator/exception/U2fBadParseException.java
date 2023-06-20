package u2f.emulator.exception;

public class U2fBadParseException extends Exception{
    public U2fBadParseException(String message) {
        super(message);
    }

    public U2fBadParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
