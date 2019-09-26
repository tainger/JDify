package io.terminus.dalaran.rest.read;

import io.terminus.dalaran.rest.DalaranClientConstants;
import org.springframework.cloud.openfeign.FeignClient;
@FeignClient(name = "DalaranServiceReadClient", url = DalaranClientConstants.DALARAN_SERVER_URL)
public interface ServiceReadClient extends ServiceReadAPI {
}
