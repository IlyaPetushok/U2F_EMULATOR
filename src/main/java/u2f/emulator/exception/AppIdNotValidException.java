package u2f.emulator.exception;

public class AppIdNotValidException extends Exception{
    public AppIdNotValidException(String message) {
        super(message);
    }

    public AppIdNotValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
