package u2f.emulator.exception;

public class U2fBadVerifySignatureException extends Exception{
    public U2fBadVerifySignatureException(String message) {
        super(message);
    }

    public U2fBadVerifySignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
