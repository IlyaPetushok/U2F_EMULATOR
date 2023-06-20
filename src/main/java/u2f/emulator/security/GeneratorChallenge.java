package u2f.emulator.security;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class GeneratorChallenge {

    private final SecureRandom random = new SecureRandom();

    public byte[] generate() {
        byte[] randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        return randomBytes;
    }
}
