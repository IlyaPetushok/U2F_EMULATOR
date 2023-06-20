package u2f.emulator.data.sign;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import u2f.emulator.exception.U2fBadVerifySignatureException;
import u2f.emulator.exception.U2fNotConfirmPresenceException;
import u2f.emulator.security.CryptoSignature;


@Getter
@Setter
public class SignResponse {
    public static final byte USER_PRESENT_FLAG = 0x01;

    private final byte userPresence;
    private final long counter;
    private final byte[] signature;
    private final CryptoSignature cryptoSignature;

    public SignResponse(byte userPresence, long counter, byte[] signature) {
        this(userPresence, counter, signature, new CryptoSignature());
    }

    public SignResponse(byte userPresence, long counter, byte[] signature, CryptoSignature cryptoSignature) {
        this.userPresence = userPresence;
        this.counter = counter;
        this.signature = signature;
        this.cryptoSignature = cryptoSignature;
    }

    public void checkSignature(String appId, String clientData, byte[] publicKey) throws U2fBadVerifySignatureException {
        byte[] signedBytes = packBytesToSign(cryptoSignature.hash(appId), userPresence, counter, cryptoSignature.hash(clientData));
        cryptoSignature.checkSignature(cryptoSignature.decodePublicKey(publicKey), signedBytes, signature);
    }

    public static byte[] packBytesToSign(byte[] appIdHash, byte userPresence, long counter, byte[] challengeHash) {
        ByteArrayDataOutput encoded = ByteStreams.newDataOutput();
        encoded.write(appIdHash);
        encoded.write(userPresence);
        encoded.writeInt((int) counter);
        encoded.write(challengeHash);
        return encoded.toByteArray();
    }

    public void checkUserPresence() throws U2fNotConfirmPresenceException {
        if (userPresence != USER_PRESENT_FLAG) {
            throw new U2fNotConfirmPresenceException("User presence invalid during signing");
        }
    }

    @Override
    public String toString() {
        return "SignResponse{" +
                "userPresence=" + userPresence +
                ", counter=" + counter +
                ", signature=" + signature +
                '}';
    }
}
