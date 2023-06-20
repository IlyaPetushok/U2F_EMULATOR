package u2f.emulator;

import com.google.common.io.BaseEncoding;
import u2f.emulator.data.client.ClientData;
import u2f.emulator.data.client.DeviceRegistration;
import u2f.emulator.data.register.RegResponse;
import u2f.emulator.data.register.RegRequest;
import u2f.emulator.data.sign.SignResponse;
import u2f.emulator.data.sign.SignRequest;
import u2f.emulator.exception.AppIdNotValidException;
import u2f.emulator.exception.DeviceCounterException;
import u2f.emulator.exception.U2fBadVerifySignatureException;
import u2f.emulator.exception.U2fNotConfirmPresenceException;
import u2f.emulator.security.GeneratorChallenge;
import u2f.emulator.valid.ValidAppId;
import u2f.emulator.valid.impl.ValidAppIdImpl;

public class U2F {
    public static final String ORIGIN = "https://www.google.com";
//    navigator.id.getAssertion для запроса на аутентификацию пользователя
//    и navigator.id.finishEnrollment для запроса на регистрацию нового пользователя
    public static final String SIGN_TYPE = "navigator.id.getAssertion";
    private static final String REGISTER_TYPE = "navigator.id.finishEnrollment";

    private final GeneratorChallenge generatorChallenge=new GeneratorChallenge();
    public static final String U2F_VERSION = "U2F_V2";
    private final ValidAppId validAppId=new ValidAppIdImpl();

    public RegRequest regBegan(String appId) throws AppIdNotValidException {
        return regBegan(appId, generatorChallenge.generate());
    }

    public RegRequest regBegan(String appId, byte[] challenge) throws AppIdNotValidException {
        validAppId.checkValidAppId(appId);
        return new RegRequest(appId, BaseEncoding.base64Url().omitPadding().encode(challenge));
    }

    public RegRequest regBegan(String appId, String challenge) throws AppIdNotValidException {
        validAppId.checkValidAppId(appId);
        return new RegRequest(appId, challenge);
    }

    public DeviceRegistration regFinish(RegRequest regRequest, RegResponse regResponse) {
        ClientData clientData = new ClientData(REGISTER_TYPE, regRequest.getChallenge(), ORIGIN);
        regResponse.checkSignature(regRequest, clientData.getRawClientData());
        return regResponse.createDevice();
    }


    public SignRequest signBegan(String appId, DeviceRegistration deviceRegistration) {
        return signBegan(appId, deviceRegistration, generatorChallenge.generate());
    }

    public SignRequest signBegan(String appId, DeviceRegistration deviceRegistration, byte[] challenge) {
        return new SignRequest(appId,BaseEncoding.base64Url().omitPadding().encode(challenge),deviceRegistration.getDescriptor());
    }

    public SignRequest signBegan(String appId, DeviceRegistration deviceRegistration, String challenge) {
        return new SignRequest(appId,challenge,deviceRegistration.getDescriptor());
    }


    public void signFinish(SignRequest signRequest, SignResponse signResponse, DeviceRegistration deviceRegistration){
        ClientData clientData=new ClientData(SIGN_TYPE,signRequest.getChallenge(),ORIGIN);
        try {
        signResponse.checkSignature(signRequest.getAppId(), clientData.getRawClientData(), BaseEncoding.base64Url().decode(deviceRegistration.getPublicKey()));
            signResponse.checkUserPresence();
            deviceRegistration.checkAndUpdateCounter(signResponse.getCounter());
        } catch (DeviceCounterException | U2fNotConfirmPresenceException | U2fBadVerifySignatureException e) {
            e.printStackTrace();
        }
    }
}
