package u2f.emulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class DeviceDTORegistration {
    private String descriptor;
    private String publicKey;
    private String attestationCert;
    private long counter;
}
