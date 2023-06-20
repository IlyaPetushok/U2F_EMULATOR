package u2f.emulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignDTOFullInfo {
    private ClientDTOData clientData;
    private DeviceDTORegistration deviceRegistration;
    private String appId;
//    private String challenge;
}
