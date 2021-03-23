package io.terminus.dalaran.core.component.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;


public interface AuthenticatorConfig<T> {

    @JSONField(serialize = false)
    @JsonIgnore
    T getAuthenticator();

    void setAuthenticator(T authenticator);

    String getAuthenticatorId();

    void setAuthenticatorId(String authenticatorId);
}
