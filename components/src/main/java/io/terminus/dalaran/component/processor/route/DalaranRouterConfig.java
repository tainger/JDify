package io.terminus.dalaran.component.processor.route;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import io.terminus.dalaran.config.OutModelConfig;
import io.terminus.dalaran.model.ProcessorModel;
import lombok.Data;

import java.util.List;

@Data
public class DalaranRouterConfig extends OutModelConfig {

    @ConfigFieldInfo(label = "路由列表", inputType = FieldInputType.Routes)
    private List<Route> routes;

    @Data
    public class Route {

        private String displayName;

        private String expression;

        private String fragmentUri;

        private List<ProcessorModel> pipeline;
    }
}
