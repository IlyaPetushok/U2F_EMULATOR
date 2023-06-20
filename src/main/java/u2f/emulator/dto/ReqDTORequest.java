package u2f.emulator.dto;

import lombok.*;
import u2f.emulator.U2F;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReqDTORequest {
    private String appId;
    private String challenge;
    private final String version= U2F.U2F_VERSION;
}
