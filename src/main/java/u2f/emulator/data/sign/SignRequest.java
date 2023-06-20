package u2f.emulator.data.sign;

import lombok.Getter;
import lombok.Setter;
import u2f.emulator.U2F;

@Getter
@Setter
public class SignRequest {

    private String version;

    private String challenge;

    private String appId;

    private String descriptor;

    public SignRequest() {
    }

    public SignRequest(String challenge, String appId, String descriptor) {
        this.version = U2F.U2F_VERSION;
        this.challenge = challenge;
        this.appId = appId;
        this.descriptor = descriptor;
    }

}
