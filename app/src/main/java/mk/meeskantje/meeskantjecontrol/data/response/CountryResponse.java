package mk.meeskantje.meeskantjecontrol.data.response;

import mk.meeskantje.meeskantjecontrol.model.Country;

public interface CountryResponse extends ProviderResponse {
    void response(Country country);
}
