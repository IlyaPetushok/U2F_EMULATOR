package u2f.emulator.security;

import com.google.common.io.BaseEncoding;
import com.yubico.u2f.data.messages.key.util.CertificateParser;

import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Scanner;

public class GnubbyKey {
    public static final BaseEncoding BASE64 = BaseEncoding.base64();

    public static final X509Certificate ATTESTATION_CERTIFICATE = fetchCertificate(GnubbyKey.class.getResourceAsStream("/attestation-certificate.der"));

    public static X509Certificate fetchCertificate(InputStream resourceAsStream) {
        Scanner in = new Scanner(resourceAsStream);
        String base64String = in.nextLine();
        return parseCertificate(BASE64.decode(base64String));
    }

    public static X509Certificate parseCertificate(byte[] encodedDerCertificate) {
        try {
            return CertificateParser.parseDer(encodedDerCertificate);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }
}
