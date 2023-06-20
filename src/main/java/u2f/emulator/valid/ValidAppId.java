package u2f.emulator.valid;

import u2f.emulator.exception.AppIdNotValidException;

public interface ValidAppId {
    void checkValidAppId(String appId) throws AppIdNotValidException;
}
