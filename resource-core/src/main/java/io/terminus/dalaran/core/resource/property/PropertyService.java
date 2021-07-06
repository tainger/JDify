package io.terminus.dalaran.core.resource.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertyService {

    @Value("${terminus.dalaran.tenant-code}")
    private String tenantCode = "terminus";

    @Value("${terminus.dalaran.tenant-console-url}")
    private String tenantConsoleUrl = "console-url";

    @Value("${terminus.dalaran.market.host}")
    private String marketHost;

    @Value("${terminus.dalaran.market.upload}")
    private String marketUpload;

    @Value("${terminus.dalaran.market.delete.resource-relation}")
    private String deleteTenantResourceRelation;

    @Value("${terminus.dalaran.market.group}")
    private String resourceGroup;

    @Value("${terminus.dalaran.market.register}")
    private String register;

    @Value("${noticeMessage.mailNoticeCode}")
    private String mailNoticeCode;

    @Value("${noticeMessage.SMSNoticeCode}")
    private String SMSNoticeCode;

    @Value("${noticeMessage.dingAccessToken}")
    private String dingAccessToken;

    @Value("${noticeMessage.interval}")
    private Long interval;

    @Value("${draco.user-info}")
    private String userInfoUrl;


    private final String DALARAN_MARKET_UI = "DALARAN_MARKET_UI";

    public String getTenantCode() {
        return tenantCode;
    }
    public String getTenantConsoleUrl() {
        return tenantConsoleUrl;
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

    public String getDeleteTenantResourceRelation() {
        return deleteTenantResourceRelation;
    }

    public String getMarketUi() {
        return System.getenv(DALARAN_MARKET_UI);
    }

    public String getMailNoticeCode() {
        return mailNoticeCode;
    }

    public String getSMSNoticeCode() {
        return SMSNoticeCode;
    }

    public String getRegister() {
        return register;
    }

    public Long getInterval() {
        return interval;
    }

    public String getUserInfoUrl() {
        return userInfoUrl;
    }

    public void setUserInfoUrl(String userInfoUrl) {
        this.userInfoUrl = userInfoUrl;
    }
}
