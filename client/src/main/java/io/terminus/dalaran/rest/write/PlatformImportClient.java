package io.terminus.dalaran.rest.write;

import io.terminus.dalaran.rest.DalaranClientConstants;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "DalaranPlatformImportClient", url = DalaranClientConstants.DALARAN_SERVER_URL)
public interface PlatformImportClient extends PlatformImportAPI {
}
