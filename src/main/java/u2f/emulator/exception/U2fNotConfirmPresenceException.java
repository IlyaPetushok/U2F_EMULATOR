package u2f.emulator.exception;

public class U2fNotConfirmPresenceException extends Exception{
    public U2fNotConfirmPresenceException(String message) {
        super(message);
    }

    public U2fNotConfirmPresenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
