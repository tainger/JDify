package io.terminus.dalaran.component.processor.multicast;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import lombok.Data;

import java.util.List;

@Data
class ScatterGatherConfig extends OutModelConfig {

    @ConfigFieldInfo(label = "分支列表", inputType = FieldInputType.Branches)
    private List<Branch> branches;

    @Data
    class Branch {
        private String displayName;

        private List<ProcessorEntity> pipeline;
    }
}
