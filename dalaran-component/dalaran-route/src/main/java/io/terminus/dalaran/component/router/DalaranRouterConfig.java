package io.terminus.dalaran.component.router;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import io.terminus.dalaran.model.component.ProcessorRouteInfo;
import lombok.Data;

import java.util.List;

@Data
class DalaranRouterConfig extends OutModelConfig {

    @ConfigFieldInfo(label = "路由列表", inputType = FieldInputType.Routes)
    private List<Route> routes;

    @Data
    class Route {
        private String displayName;

        private String expression;

        private List<ProcessorRouteInfo> pipeline;
    }

    public List<Route> getRoutes() {
        return routes;
    }
}
