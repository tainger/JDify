package io.terminus.dalaran.core.resource.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertyService {

    @Value("${terminus.dalaran.tenant-code}")
    private String tenantCode = "terminus";

    @Value("${terminus.dalaran.market.host}")
    private String marketHost;

    @Value("${terminus.dalaran.market.upload}")
    private String marketUpload;

    @Value("${terminus.dalaran.market.group}")
    private String resourceGroup;

    public String getTenantCode() {
        return tenantCode;
    }

    public String getMarketHost() {
        return marketHost;
    }

    public String getMarketUpload() {
        return marketUpload;
    }

    public String getResourceGroup() {
        return resourceGroup;
    }
}
