package u2f.emulator.exception;

public class DeviceCounterException extends Exception{
    public DeviceCounterException(String message) {
        super(message);
    }

    public DeviceCounterException(String message, Throwable cause) {
        super(message, cause);
    }
}
