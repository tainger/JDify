package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.DalaranBasicComponent;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.DynamicModel;
import lombok.Data;

@Data
@DynamicModel(value = "Limiter")
public class BasicLimiter implements DalaranBasicComponent {

    @ConfigFieldInfo(label = "组件名称", inputType = FieldInputType.String)
    private String name;

    @ConfigFieldInfo(label = "限流器类型", inputType = FieldInputType.LimiterSelector, path = "/api/platform/limiter", dynamic = true)
    private String limiterType;
}
