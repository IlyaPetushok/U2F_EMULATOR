package u2f.emulator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import u2f.emulator.data.register.RegRequest;
import u2f.emulator.data.sign.SignRequest;
import u2f.emulator.dto.DeviceDTORegistration;
import u2f.emulator.dto.ReqDTORequest;
import u2f.emulator.dto.SignDTOFullInfo;
import u2f.emulator.dto.SignDTORequest;
import u2f.emulator.service.AuthorizationU2fService;
import u2f.emulator.service.RegistrationU2fService;

@RestController
@RequestMapping("/u2f")
public class ControllerU2F {
    private final RegistrationU2fService registrationU2FService;
    private final AuthorizationU2fService authorizationU2fService;

    @Autowired
    public ControllerU2F(RegistrationU2fService registrationU2FService,AuthorizationU2fService authorizationU2fService) {
        this.authorizationU2fService=authorizationU2fService;
        this.registrationU2FService = registrationU2FService;
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registrationU2f(@RequestBody ReqDTORequest reqDTORequest) throws Exception {
        RegRequest regRequest=registrationU2FService.regServiceBegan(reqDTORequest);
        DeviceDTORegistration deviceDTORegistration=registrationU2FService.regServiceFinish(regRequest);
        return new ResponseEntity<>(deviceDTORegistration, HttpStatus.OK);
    }

    @PostMapping("/authorization")
    public ResponseEntity<?> authorizationU2f(@RequestBody SignDTOFullInfo signDTOFullInfo){
        SignDTORequest signRequest=authorizationU2fService.authorizationServiceBegan(signDTOFullInfo.getAppId(), signDTOFullInfo.getDeviceRegistration(), signDTOFullInfo.getClientData().getChallenge());
        boolean flag=authorizationU2fService.authorizationServiceFinish(signRequest, signDTOFullInfo.getDeviceRegistration(), signDTOFullInfo.getClientData());
        return new ResponseEntity<>(flag,HttpStatus.OK);
    }


}
