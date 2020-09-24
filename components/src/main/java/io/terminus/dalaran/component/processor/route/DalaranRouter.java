package io.terminus.dalaran.component.processor.route;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranProcessorConfigCustomConverter;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.model.component.ComponentModel;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowFragment;
import lombok.val;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.terminus.dalaran.DalaranConstants.DELIMITER;

@Processor(
        value = "router",
        order = 15,
        configType = DalaranRouterConfig.class,
        description = "路由选择节点：能够通过设置判定条件控制流程的多分支执行，类似编程逻辑中的switch, if/else"
)
public class DalaranRouter implements DalaranProcessor<Map<String, String>>, DalaranProcessorConfigCustomConverter<DalaranRouterConfig, Map<String, String>> {

    private static final String OTHERWISE_EXPRESSION = "OTHERWISE";

    @Autowired
    private DalaranContext dalaranContext;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    public void configure(ProcessorDefinition route, Map<String, String> config) {
        ChoiceDefinition choiceDefinition = route.choice();
        for (Map.Entry<String, String> routeItem : config.entrySet()) {
            if (OTHERWISE_EXPRESSION.equals(routeItem.getKey())) {
                choiceDefinition.otherwise();
            } else {
                choiceDefinition.when().mvel(routeItem.getKey());
            }
            if (routeItem.getValue() != null) {
                choiceDefinition.to(routeItem.getValue());
            }
        }
        choiceDefinition.end();
    }

    /**
     * 将路由配置转化为多条路由片段, 并返回路由条件和片段路径
     * return: Map<Expression, RouteUri>
     */
    @Override
    public Map<String, String> convert(DalaranRouterConfig config, ComponentModel component, BasicFlow flow) {
        List<DalaranRouterConfig.Route> routes = config.getRoutes();
        Map<String, String> routeMapper = new HashMap<>();

        for (int i = 0; i < routes.size(); i++) {
            val route = routes.get(i);

            if (route.getPipeline().isEmpty()) {
                routeMapper.put(route.getExpression(), null);
                continue;
            }

            String fragmentId = component.getId() + DELIMITER + i;
            FlowFragment fragment = resourceBuilder.buildFlowFragment(route.getPipeline(), component.getInModel(),
                    component.getOutModel(), flow.getId(), fragmentId, flow.isTracing());
            dalaranContext.addFragmentFlow(fragment);

            routeMapper.put(route.getExpression(), fragment.getDirectRouteUri());
        }
        return routeMapper;
    }

}
