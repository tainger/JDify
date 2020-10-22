package io.terminus.dalaran.component.limiter;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.common.LimitOperation;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.Limiter;
import lombok.Data;

@Data
@Limiter(value = "BasicLimiter")
public class DalaranLimiter {

//    @ConfigFieldInfo(label = "开启限流", inputType = FieldInputType.Switch, defaultValue = "false")
//    private boolean enableLimit = false;

    @ConfigFieldInfo(label = "限流周期", inputType = FieldInputType.Integer, required = false)
    private Integer limitPeriod = 1000;

    @ConfigFieldInfo(label = "请求限制", inputType = FieldInputType.Integer, required = false)
    private Integer limitRequestNum = 50;

    @ConfigFieldInfo(label = "限流操作", inputType = FieldInputType.Radio, defaultValue = "DELAY")
    private LimitOperation operation = LimitOperation.DELAY;

//    @ConfigFieldInfo(label = "开启熔断", inputType = FieldInputType.Switch, defaultValue = "false")
//    private boolean enableBreaker = false;

    @ConfigFieldInfo(label = "异常占比", inputType = FieldInputType.Integer, required = false)
    private Integer circuitBreakerErrorPercentage = 50;

    @ConfigFieldInfo(label = "最小请求数", inputType = FieldInputType.Integer, required = false)
    private Integer circuitBreakerRequestVolumeThreshold = 20;

    @ConfigFieldInfo(label = "熔断重试间隔(ms)", inputType = FieldInputType.Integer, required = false)
    private Integer circuitBreakerSleepWindowInMilliseconds = 5000;
}
