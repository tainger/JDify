package io.terminus.dalaran.component.processor.route;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranProcessorConfigCustomConverter;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.component.model.ComponentModel;
import io.terminus.dalaran.core.component.model.ProcessorModel;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.flow.model.BasicFlow;
import io.terminus.dalaran.core.flow.model.FlowFragment;
import io.terminus.dalaran.core.model.MessageModel;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import lombok.val;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.terminus.dalaran.core.DalaranConstants.DELIMITER;

@Processor(value = "router", configType = DalaranRouterConfig.class, inputSerializeType = BodySerializeType.Object)
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
            choiceDefinition.to(routeItem.getValue());
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
            FlowFragment fragment = new FlowFragment();

            MessageModel fragmentLastOutModel = config.getInModel();
            List<ProcessorModel> pipeline = new ArrayList<>();
            for (ProcessorEntity processorEntity : route.getPipeline()) {
                val processorModel = resourceBuilder.buildProcessorModel(processorEntity, fragmentLastOutModel, flow);
                fragmentLastOutModel = processorModel.getOutModel();
                pipeline.add(processorModel);
            }

            fragment.setId(flow.getId());
            fragment.setFragmentId(component.getId() + DELIMITER + i);
            fragment.setPipeline(pipeline);
            fragment.setInModel(config.getInModel());
            fragment.setOutModel(fragmentLastOutModel);

            dalaranContext.addFragmentFlow(fragment);

            routeMapper.put(route.getExpression(), fragment.getDirectRouteUri());
        }
        return routeMapper;
    }
}
