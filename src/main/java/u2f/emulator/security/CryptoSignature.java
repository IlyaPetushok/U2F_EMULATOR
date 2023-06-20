package u2f.emulator.security;

import com.google.common.io.BaseEncoding;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import u2f.emulator.exception.U2fBadParseException;
import u2f.emulator.exception.U2fBadVerifySignatureException;
import java.security.*;
import java.security.cert.X509Certificate;

public class CryptoSignature {

    private static final Provider provider = new BouncyCastleProvider();

    public void checkSignature(X509Certificate attestationCertificate, byte[] signedBytes, byte[] signature) throws U2fBadVerifySignatureException {
        checkSignature(attestationCertificate.getPublicKey(), signedBytes, signature);
    }

    public void checkSignature(PublicKey publicKey, byte[] signedBytes, byte[] signature) throws  U2fBadVerifySignatureException {
        Signature ecdsaSignature;
        try {
            ecdsaSignature = Signature.getInstance("SHA256withECDSA", provider);
            ecdsaSignature.initVerify(publicKey);
            ecdsaSignature.update(signedBytes);
            if (!ecdsaSignature.verify(signature)) {
                throw new U2fBadVerifySignatureException(String.format(
                        "Signature is invalid. Public key: %s, signed data: %s , signature: %s",
                        publicKey,
                        BaseEncoding.base64Url().omitPadding().encode(signedBytes),
                        BaseEncoding.base64Url().omitPadding().encode(signature)
                ));
            }
        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    public PublicKey decodePublicKey(byte[] encodedPublicKey) {
        try {
            X9ECParameters curve = SECNamedCurves.getByName("secp256r1");
            ECPoint point;
            try {
                point = curve.getCurve().decodePoint(encodedPublicKey);
            } catch (RuntimeException e) {
                throw new U2fBadParseException("Could not parse user public key", e);
            }

            return KeyFactory.getInstance("ECDSA", provider).generatePublic(
                    new ECPublicKeySpec(point,
                            new ECParameterSpec(
                                    curve.getCurve(),
                                    curve.getG(),
                                    curve.getN(),
                                    curve.getH()
                            )
                    )
            );
        } catch (GeneralSecurityException | U2fBadParseException e) {
            throw new RuntimeException("Failed to decode public key: " + BaseEncoding.base64Url().encode(encodedPublicKey), e);
        }
    }

    public byte[] hash(byte[] bytes) {
        try {
            return MessageDigest.getInstance("SHA-256", provider).digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] hash(String str) {
        return hash(str.getBytes());
    }
}
