package u2f.emulator.dto;

import lombok.*;
import u2f.emulator.U2F;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SignDTORequest {

    private String version;
    private String challenge;
    private String appId;
    private String descriptor;

    public SignDTORequest(String challenge, String appId, String descriptor) {
        this.version = U2F.U2F_VERSION;
        this.challenge = challenge;
        this.appId = appId;
        this.descriptor = descriptor;
    }
}
