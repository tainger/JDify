package io.terminus.dalaran.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "terminus.dalaran")
public class DalaranSdkProperties {

    private boolean enable;

    private String runtimeUrl;

    private String[] basePackages;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getRuntimeUrl() {
        return runtimeUrl;
    }

    public void setRuntimeUrl(String runtimeUrl) {
        this.runtimeUrl = runtimeUrl;
    }

    public String[] getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(String[] basePackages) {
        this.basePackages = basePackages;
    }
}
