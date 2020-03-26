package mk.meeskantje.meeskantjecontrol.data.response;

import mk.meeskantje.meeskantjecontrol.model.Sensor;

public interface SensorResponse extends ProviderResponse {
    void response(Sensor sensor);
}
