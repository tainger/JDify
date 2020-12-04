package io.terminus.dalaran.component.multicast;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranProcessorConfigCustomConverter;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.context.DalaranModelTypeContext;
import io.terminus.dalaran.core.flow.DalaranFlowBuilder;
import io.terminus.dalaran.core.flow.DalaranRoute;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.model.component.ComponentModel;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowFragment;
import lombok.val;
import org.apache.camel.builder.Builder;
import org.apache.camel.model.MulticastDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static io.terminus.dalaran.DalaranConstants.BRANCH_FLOW_NAME_HEADER;
import static io.terminus.dalaran.DalaranConstants.DELIMITER;

@Processor(
        value = "scatter-gather",
        configType = ScatterGatherConfig.class,
        description = "并行节点：使用该组件能够达到多分支并行的效果"
)
public class ScatterGather implements DalaranProcessor<List<String>>, DalaranProcessorConfigCustomConverter<ScatterGatherConfig, List<String>> {

    @Autowired
    private DalaranContext<DalaranRoute> dalaranContext;

    @Autowired
    private DalaranFlowBuilder<DalaranRoute> flowBuilder;

    @Autowired
    private DalaranModelTypeContext converterContext;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    public void configure(ProcessorDefinition route, List<String> branches) {
        MulticastDefinition multicastDefinition = route.multicast(new ScatterGatherAggregationStrategy(), true);
        for (String fragmentUri : branches) {
            multicastDefinition.to(fragmentUri);
        }
        multicastDefinition.end();

    }

    /**
     * 将分支配置转化为多条流程片段, 并返回片段路径
     * return: List<RouteUri>
     */
    // TODO 这里还少一个合并, 就是每个分支的出参最后合并为 scatter gather 的出参
    public List<String> convert(ScatterGatherConfig config, ComponentModel component, BasicFlow flow) {
        List<ScatterGatherConfig.Branch> branches = config.getBranches();
        List<String> branchList = new ArrayList<>();

        for (int i = 0; i < branches.size(); i++) {
            val branch = branches.get(i);
            String fragmentId = component.getId() + DELIMITER + i;
            FlowFragment fragment = resourceBuilder.buildFlowFragment(branch.getPipeline(), component.getInModel(),
                    component.getOutModel(), flow.getId(), fragmentId, flow.isTracing());
            DalaranRoute fragmentRoute = flowBuilder.buildFlowFragment(fragment);

            // 因为后面会有一个聚合处理, 所以一定要输出为 Object, 否则 Mapper 不好处理
            if (DalaranConstants.OBJECT_MODEL_TYPE.equalsIgnoreCase(fragmentRoute.getLastBodyType())) {
                converterContext.toObject(fragmentRoute, fragmentRoute.getLastOutModel());
            }

            // 加入分支标识, 在后面聚合的时候用作处理
            fragmentRoute.setProperty(BRANCH_FLOW_NAME_HEADER, Builder.constant(branch.getDisplayName()));

            dalaranContext.addRoute(fragmentRoute);

            branchList.add(fragment.getDirectRouteUri());
        }
        return branchList;
    }
}
