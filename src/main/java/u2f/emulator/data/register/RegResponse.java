package u2f.emulator.data.register;

import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import u2f.emulator.data.client.DeviceRegistration;
import u2f.emulator.exception.U2fBadVerifySignatureException;
import u2f.emulator.security.CryptoSignature;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

@Getter
@Setter
@ToString
public class RegResponse {

    private final byte[] userPublicKey;
    private final byte[] descriptor;
    private final X509Certificate attestationCert;
    private final byte[] signature;


    public RegResponse(byte[] userPublicKey, byte[] descriptor, X509Certificate attestationCert, byte[] signature) {
        this.userPublicKey = userPublicKey;
        this.descriptor = descriptor;
        this.attestationCert = attestationCert;
        this.signature = signature;
    }

    public static byte[] packBytesToSign(byte[] appIdHash, byte[] clientDataHash, byte[] keyHandle, byte[] userPublicKey) {
        ByteArrayDataOutput encoded = ByteStreams.newDataOutput();
//        Упакованные данные состоят из одного байта (0x05) и слеж того же формата
        encoded.write((byte) 0x05);
        encoded.write(appIdHash);
        encoded.write(clientDataHash);
        encoded.write(keyHandle);
        encoded.write(userPublicKey);
        return encoded.toByteArray();
    }

    public void checkSignature(RegRequest regRequest, String clientData) {
        CryptoSignature cryptoSignature =new CryptoSignature();
        byte[] signedBytes = packBytesToSign(cryptoSignature.hash(regRequest.getAppId()), cryptoSignature.hash(clientData), descriptor, userPublicKey);
        try {
            cryptoSignature.checkSignature(attestationCert, signedBytes, signature);
        } catch (U2fBadVerifySignatureException e) {
            e.printStackTrace();
        }
    }

    public DeviceRegistration createDevice() {
        try {
            return new DeviceRegistration(
                    BaseEncoding.base64().encode(descriptor),
                    BaseEncoding.base64Url().omitPadding().encode(userPublicKey),
                    attestationCert,
                    DeviceRegistration.COUNTER_VALUE
            );
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
