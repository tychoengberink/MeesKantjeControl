package mk.meeskantje.meeskantjecontrol.data.response;

import mk.meeskantje.meeskantjecontrol.model.SensorLog;

public interface SensorLogResponse extends ProviderResponse {
    void response(SensorLog sensorLog);
}
