package u2f.emulator.valid.impl;

import com.google.common.net.InetAddresses;
import org.springframework.stereotype.Component;
import u2f.emulator.exception.AppIdNotValidException;
import u2f.emulator.valid.ValidAppId;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class ValidAppIdImpl implements ValidAppId {

    public void checkValidAppId(String appId) throws AppIdNotValidException {
        if(!appId.contains(":")) {
            throw new AppIdNotValidException("App Id doesnt look valid");
        }
        if(appId.startsWith("https://")) {
             checkValidUrl(appId);
        }else {
            throw new AppIdNotValidException("Doesnt supported app id. Use HTTPS!!!");
        }
    }

    private  void checkValidUrl(String appId) throws AppIdNotValidException {
        URI url = null;
        try {
            url = new URI(appId);
        } catch (URISyntaxException e) {
            throw new AppIdNotValidException("App ID looks like a HTTPS URL, but has syntax errors.", e);
        }
        if("/".equals(url.getPath()) && InetAddresses.isInetAddress(url.getAuthority()) || (url.getHost() != null && InetAddresses.isInetAddress(url.getHost()))) {
            throw new AppIdNotValidException("Ip-address is not valid");
        }
    }
}
