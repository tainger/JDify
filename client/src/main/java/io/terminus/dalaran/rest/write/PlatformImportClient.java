package io.terminus.dalaran.rest.write;

import org.springframework.cloud.openfeign.FeignClient;

import io.terminus.dalaran.rest.DalaranClientConstants;

@FeignClient(name = "DalaranPlatformImportClient", url = DalaranClientConstants.DALARAN_SERVER_URL)
public interface PlatformImportClient extends PlatformImportAPI {
}
