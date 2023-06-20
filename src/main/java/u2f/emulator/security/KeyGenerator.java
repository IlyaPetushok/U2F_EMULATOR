package u2f.emulator.security;

import com.google.common.io.BaseEncoding;
import com.yubico.u2f.data.messages.key.util.ByteInputStream;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Component;
import u2f.emulator.KeyboardListener;
import u2f.emulator.data.register.RegResponse;
import u2f.emulator.data.register.RegRequest;
import u2f.emulator.data.sign.SignResponse;
import u2f.emulator.data.sign.SignRequest;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;


@Component
public class KeyGenerator {

    private static final Map<String, KeyPair> keys = new HashMap<>();
    private long deviceCounter = 0;


    public RegResponse generateKeyForRegistration(RegRequest regRequest) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] descriptor = new byte[64];
        byte[] appIdSHA256 = md.digest(regRequest.getAppId().getBytes(StandardCharsets.UTF_8));
        byte[] challengeSHA256 = md.digest(regRequest.getChallenge().getBytes(StandardCharsets.UTF_8));

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(descriptor);
        ECParameterSpec ecParameterSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
//        не работает без provider провайдер содержит реализации методов для создания ключей ECDSA и других криптографических операций
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
        keyPairGenerator.initialize(ecParameterSpec, secureRandom);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        String des=new String(descriptor);
        keys.put(BaseEncoding.base64().encode(descriptor), keyPair);



        X509Certificate certificate=generateCertificate(keyPair);

        byte[] publicKey = stripMetaData(keyPair.getPublic().getEncoded());

        byte[] signedData = RegResponse.packBytesToSign(appIdSHA256, challengeSHA256, descriptor, publicKey);

        byte[] signature = sign(signedData, keyPair.getPrivate());

        return new RegResponse(publicKey, descriptor, certificate, signature);
    }

    public SignResponse sign(SignRequest signRequest) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] appIdSHA256 = md.digest(signRequest.getAppId().getBytes(StandardCharsets.UTF_8));
        byte[] challengeSHA256 = md.digest(signRequest.getChallenge().getBytes(StandardCharsets.UTF_8));
        String descriptor = signRequest.getDescriptor();

        KeyPair keyPair = keys.get(descriptor);
        long counter = ++deviceCounter;

        byte userPresence=SignResponse.USER_PRESENT_FLAG;
        KeyboardListener listener = new KeyboardListener();
        if(!listener.keyPressed){
            userPresence=0x00; //сигнализирует, что в момент создания подписи пользователь не было на месте и согласился на доказательство своей личности.
        }

        byte[] signedData = SignResponse.packBytesToSign(appIdSHA256, userPresence, counter, challengeSHA256);

        byte[] signature = sign(signedData, keyPair.getPrivate());

        return new SignResponse(userPresence, counter, signature);
    }

    private byte[] stripMetaData(byte[] a) {
        ByteInputStream bis = new ByteInputStream(a);
        try {
            bis.read(3);
            bis.read(bis.readUnsigned() + 1);
            int keyLength = bis.readUnsigned();
            bis.read(1);
            return bis.read(keyLength - 1);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private byte[] sign(byte[] signedData, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withECDSA", new BouncyCastleProvider());
        signature.initSign(privateKey);
        signature.update(signedData);
        return signature.sign();
    }

    private X509Certificate generateCertificate(KeyPair keyPair) throws CertificateException, OperatorCreationException {
        X500Name issuerName = new X500Name("CN=Test Certificate");//информацией об издателе сертификата
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuerName,
                //случайное число, которое будет использоваться в сертификате в качестве серийного номера.
                BigInteger.valueOf(new SecureRandom().nextInt()),
                new Date(),
                new Date(System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)),
                issuerName,
                keyPair.getPublic());
        //      подписать сгенерированный сертификат. Здесь используется алгоритм SHA-256 с ECDSA для создания подписи
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withECDSA").build(keyPair.getPrivate());
        X509CertificateHolder certHolder = certBuilder.build(signer);

        X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(certHolder);

        System.out.println("---------------Began_Certificate---------------");
        System.out.println(certificate);
        System.out.println("---------------End_Certificate-----------------");

        try (FileOutputStream fos =new FileOutputStream("attestation-certificate.der")){
            byte[] buffer = certificate.getEncoded();
            fos.write(buffer, 0, buffer.length);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        System.out.println("Certificate written to file successfully.");
        System.out.println();

        return certificate;
    }

}

