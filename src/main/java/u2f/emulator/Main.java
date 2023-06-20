package u2f.emulator;

import u2f.emulator.data.client.DeviceRegistration;
import u2f.emulator.data.register.RegResponse;
import u2f.emulator.data.register.RegRequest;
import u2f.emulator.data.sign.SignResponse;
import u2f.emulator.data.sign.SignRequest;
import u2f.emulator.security.KeyGenerator;

public class Main {
    private static final String APP_ID="https://www.google.com";
    private static final U2F u2f=new U2F();
    KeyGenerator keyGenerator=new KeyGenerator();


    public static void main(String[] args) throws Exception {
        System.out.println("Start registration...");
        RegRequest regRequest =u2f.regBegan(APP_ID);
        System.out.println("data register"+ regRequest);

        KeyGenerator keyGenerator=new KeyGenerator();
        RegResponse regResponse =keyGenerator.generateKeyForRegistration(regRequest);
        System.out.println("data response "+ regResponse);

        System.out.println();
        System.out.println("create DeviceRegistration....");
        DeviceRegistration deviceRegistration= u2f.regFinish(regRequest, regResponse);


        System.out.println("data device registration: \n"+deviceRegistration);


        System.out.println("start sign...");
        SignRequest signRequest=u2f.signBegan(APP_ID,deviceRegistration);

        System.out.println("data sign request:\n"+signRequest);
        System.out.println();

        SignResponse signResponse =keyGenerator.sign(signRequest);
        System.out.println("data sign response:"+signResponse);

        u2f.signFinish(signRequest, signResponse,deviceRegistration);

        System.out.println("sign finish...");
        System.out.println("device:"+deviceRegistration);
    }
}
