package io.terminus.dalaran.rest.read;

import io.terminus.dalaran.rest.DalaranClientConstants;
import org.springframework.cloud.openfeign.FeignClient;
@FeignClient(name = "DalaranConnectorReadClient", url = DalaranClientConstants.DALARAN_SERVER_URL)
public interface ConnectorReadClient extends ConnectorReadAPI {
}
