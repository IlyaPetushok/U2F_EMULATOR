package u2f.emulator.data.register;

import lombok.*;
import u2f.emulator.U2F;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegRequest {

    private String challenge;
    private String version;
    private String appId;


    public RegRequest(@NonNull String appId, @NonNull String challenge) {
        this.challenge = challenge;
        this.version = U2F.U2F_VERSION;
        this.appId = appId;
    }
}
