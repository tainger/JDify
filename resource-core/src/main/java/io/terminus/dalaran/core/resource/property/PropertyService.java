package io.terminus.dalaran.core.resource.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertyService {

    @Value("${terminus.dalaran.tenant-code}")
    private String tenantCode;

    @Value("${terminus.dalaran.market.host}")
    private String marketHost;

    @Value("${terminus.dalaran.market.upload}")
    private String marketUpload;

    private static String DALARAN_TENANT_CODE = "dalaran_tenant_code";

    public String getTenantCode() {
        return tenantCode;
    }

    public String getMarketHost() {
        return marketHost;
    }

    public String getMarketUpload() {
        return marketUpload;
    }
}
