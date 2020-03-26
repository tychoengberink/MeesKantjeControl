package mk.meeskantje.meeskantjecontrol.data.response;

import mk.meeskantje.meeskantjecontrol.model.Drone;

public interface DroneResponse extends ProviderResponse {
    void response(Drone drone);
}
