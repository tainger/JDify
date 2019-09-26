package io.terminus.dalaran.rest.write;

import io.terminus.dalaran.rest.DalaranClientConstants;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "DalaranReleaseWriteClient", url = DalaranClientConstants.DALARAN_SERVER_URL)
public interface ReleaseWriteClient extends ReleaseWriteAPI {
}
