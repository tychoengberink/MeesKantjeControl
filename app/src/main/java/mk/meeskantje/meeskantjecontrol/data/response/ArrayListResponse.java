package mk.meeskantje.meeskantjecontrol.data.response;

import java.util.ArrayList;

public interface ArrayListResponse extends ProviderResponse {
    void response(ArrayList<?> data);
}
