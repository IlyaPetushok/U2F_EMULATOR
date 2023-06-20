package u2f.emulator.service;

import com.google.common.io.BaseEncoding;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import u2f.emulator.data.client.DeviceRegistration;
import u2f.emulator.data.register.RegRequest;
import u2f.emulator.data.register.RegResponse;
import u2f.emulator.dto.DeviceDTORegistration;
import u2f.emulator.dto.ReqDTORequest;
import u2f.emulator.exception.AppIdNotValidException;
import u2f.emulator.security.GeneratorChallenge;
import u2f.emulator.security.KeyGenerator;
import u2f.emulator.valid.ValidAppId;

@Service
public class RegistrationU2fService {
    private final ModelMapper modelMapper;
    private final GeneratorChallenge generatorChallenge;
    private final ValidAppId validAppId;
    private final KeyGenerator keyGenerator;

    @Autowired
    public RegistrationU2fService(ModelMapper modelMapper, GeneratorChallenge generatorChallenge, ValidAppId validAppId,KeyGenerator keyGenerator) {
        this.modelMapper = modelMapper;
        this.generatorChallenge = generatorChallenge;
        this.validAppId = validAppId;
        this.keyGenerator = keyGenerator;
    }

    public RegRequest regServiceBegan(ReqDTORequest reqDTORequest){
        try {
            validAppId.checkValidAppId(reqDTORequest.getAppId());
        } catch (AppIdNotValidException e) {
            e.printStackTrace();
        }
        if(reqDTORequest.getChallenge().isEmpty()){
            reqDTORequest.setChallenge(BaseEncoding.base64Url().omitPadding().encode(generatorChallenge.generate()));
        }
        return modelMapper.map(reqDTORequest,RegRequest.class);
    }

    public DeviceDTORegistration regServiceFinish(RegRequest regRequest) throws Exception {
        RegResponse regResponse=keyGenerator.generateKeyForRegistration(regRequest);
        DeviceRegistration deviceRegistration=regResponse.createDevice();
        return modelMapper.map(deviceRegistration, DeviceDTORegistration.class);
    }
}
