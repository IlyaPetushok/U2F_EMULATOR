package u2f.emulator.data.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.BaseEncoding;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;


@Getter
@Setter
public class ClientData {
    private static final String TYPE_PARAM = "typ";
    private static final String CHALLENGE_PARAM = "challenge";
    private static final String ORIGIN_PARAM = "origin";

    private String type;
    private String challenge;
    private String origin;
    private String rawClientData;

    public ClientData() {
    }

    public ClientData(String type, String challenge, String origin) {
        this.type = type;
        this.challenge = challenge;
        this.origin = origin;
        rawClientData = mapToJson(type, challenge, origin).toString();
    }

    public ClientData(String clientData) throws JsonProcessingException {
        rawClientData = new String(BaseEncoding.base64Url().decode(clientData));
        JsonNode data = new ObjectMapper().readTree(rawClientData);
        type = getString(data, TYPE_PARAM);
        challenge = getString(data, CHALLENGE_PARAM);
        origin = getString(data, ORIGIN_PARAM);
    }

    public JSONObject mapToJson(String type, String challenge, String origin) {
        JSONObject jo = new JSONObject();
        jo.put("type", type);
        jo.put("challenge", challenge);
        jo.put("origin", origin);
        return jo;
    }

    public String getRawClientData() {
        return challenge;
    }

    public String getChallenge() {
        return challenge;
    }

    private static String getString(JsonNode data, String key) {
        JsonNode node = data.get(key);
        return node.asText();
    }

}
