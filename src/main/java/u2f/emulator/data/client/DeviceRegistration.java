package u2f.emulator.data.client;

import com.google.common.base.MoreObjects;
import com.google.common.io.BaseEncoding;

import lombok.NoArgsConstructor;
import u2f.emulator.exception.DeviceCounterException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

@NoArgsConstructor
public class DeviceRegistration {
    public static final long COUNTER_VALUE = 0;

    private String descriptor;
    private String publicKey;
    private String attestationCert;
    private long counter;


    public DeviceRegistration(String descriptor, String publicKey, X509Certificate attestationCert, long counter) throws CertificateEncodingException {
        this.descriptor = descriptor;
        this.publicKey = publicKey;
        this.attestationCert = BaseEncoding.base64Url().omitPadding().encode(attestationCert.getEncoded());
        this.counter = counter;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public long getCounter() {
        return counter;
    }

    public String getAttestationCert() {
        return attestationCert;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setAttestationCert(String attestationCert) {
        this.attestationCert = attestationCert;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("\n\tDescriptor", descriptor)
                .add("\n\tPublic key", publicKey)
                .add("\n\tCounter", counter)
                .add("\n\tAttestation certificate", attestationCert+"\n")
                .toString();
    }


    public void checkAndUpdateCounter(long clientCounter) throws DeviceCounterException {
        if (clientCounter <= getCounter()) {
            throw new DeviceCounterException("Device maybe been cloned");
        }
        counter = clientCounter;
    }

}
