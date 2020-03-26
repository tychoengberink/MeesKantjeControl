package mk.meeskantje.meeskantjecontrol.data.response;

import mk.meeskantje.meeskantjecontrol.model.Coordinate;

public interface CoordinateResponse extends ProviderResponse {
    void response(Coordinate coordinate);
}
