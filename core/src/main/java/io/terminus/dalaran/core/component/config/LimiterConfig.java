package io.terminus.dalaran.core.component.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

public interface LimiterConfig<T> {

    @JSONField(serialize = false)
    @JsonIgnore
    T getLimiter();

    void setLimiter(T limiter);

    Long getLimiterId();

    void setLimiterId(Long limiterId);
}
