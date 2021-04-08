package io.terminus.dalaran.component.multicast;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import io.terminus.dalaran.model.component.ProcessorRouteInfo;
import lombok.Data;

import java.util.List;

@Data
public class ScatterGatherConfig extends OutModelConfig {

    @ConfigFieldInfo(label = "分支列表", inputType = FieldInputType.Branches)
    private List<Branch> branches;

    @Data
    public class Branch {
        private String displayName;

        private List<ProcessorRouteInfo> pipeline;
    }
}
