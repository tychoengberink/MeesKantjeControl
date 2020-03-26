package mk.meeskantje.meeskantjecontrol.data.response;

import org.json.JSONArray;

public interface JsonArrayResponse extends ProviderResponse {
    void response(JSONArray data);
}
