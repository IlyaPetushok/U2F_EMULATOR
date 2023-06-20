package u2f.emulator.service;

import com.google.common.io.BaseEncoding;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import u2f.emulator.U2F;
import u2f.emulator.data.client.ClientData;
import u2f.emulator.data.client.DeviceRegistration;
import u2f.emulator.data.sign.SignRequest;
import u2f.emulator.data.sign.SignResponse;
import u2f.emulator.dto.ClientDTOData;
import u2f.emulator.dto.DeviceDTORegistration;
import u2f.emulator.dto.SignDTORequest;
import u2f.emulator.exception.AppIdNotValidException;
import u2f.emulator.security.GeneratorChallenge;
import u2f.emulator.security.KeyGenerator;
import u2f.emulator.valid.ValidAppId;

@Service
public class AuthorizationU2fService {
    private final ValidAppId validAppId;
    private final ModelMapper modelMapper;
    private final GeneratorChallenge generatorChallenge;
    private final KeyGenerator keyGenerator;

    @Autowired
    public AuthorizationU2fService(ValidAppId validAppId, ModelMapper modelMapper, GeneratorChallenge generatorChallenge, KeyGenerator keyGenerator) {
        this.validAppId = validAppId;
        this.modelMapper = modelMapper;
        this.generatorChallenge = generatorChallenge;
        this.keyGenerator = keyGenerator;
    }

    public SignDTORequest authorizationServiceBegan(String appId, DeviceDTORegistration deviceDTORegistration, String challenge) {
        try {
            validAppId.checkValidAppId(appId);
        } catch (AppIdNotValidException e) {
            e.printStackTrace();
        }
        if (challenge.isEmpty()) {
            challenge = BaseEncoding.base64Url().omitPadding().encode(generatorChallenge.generate());
        }
        return new SignDTORequest(challenge,appId, deviceDTORegistration.getDescriptor());
    }

    public boolean authorizationServiceFinish(SignDTORequest signDTORequest, DeviceDTORegistration deviceDTORegistration, ClientDTOData clientDTOData) {
        SignRequest signRequest = modelMapper.map(signDTORequest, SignRequest.class);
        DeviceRegistration deviceRegistration = modelMapper.map(deviceDTORegistration,DeviceRegistration.class);
        try {
            SignResponse signResponse = keyGenerator.sign(signRequest);
            ClientData clientData = modelMapper.map(clientDTOData, ClientData.class);
            signResponse.checkSignature(signRequest.getAppId(), clientData.getRawClientData(), BaseEncoding.base64Url().decode(deviceRegistration.getPublicKey()));
            signResponse.checkUserPresence();
            deviceRegistration.checkAndUpdateCounter(signResponse.getCounter());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
