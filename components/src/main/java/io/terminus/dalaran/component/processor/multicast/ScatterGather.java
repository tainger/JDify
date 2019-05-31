package io.terminus.dalaran.component.processor.multicast;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranProcessorConfigCustomConverter;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.component.model.ComponentModel;
import io.terminus.dalaran.core.component.model.ProcessorModel;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.context.DalaranConverterContext;
import io.terminus.dalaran.core.flow.DalaranFlowBuilder;
import io.terminus.dalaran.core.flow.DalaranRoute;
import io.terminus.dalaran.core.flow.model.BasicFlow;
import io.terminus.dalaran.core.flow.model.FlowFragment;
import io.terminus.dalaran.core.model.MessageModel;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import lombok.val;
import org.apache.camel.builder.Builder;
import org.apache.camel.model.MulticastDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static io.terminus.dalaran.core.DalaranConstants.BRANCH_FLOW_NAME_HEADER;
import static io.terminus.dalaran.core.DalaranConstants.DELIMITER;

@Processor(value = "scatter-gather", configType = ScatterGatherConfig.class, outputSerializeType = BodySerializeType.Object)
public class ScatterGather implements DalaranProcessor<List<String>>, DalaranProcessorConfigCustomConverter<ScatterGatherConfig, List<String>> {

    @Autowired
    private DalaranContext dalaranContext;

    @Autowired
    private DalaranConverterContext converterContext;

    @Autowired
    private DalaranFlowBuilder<DalaranRoute> flowBuilder;

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
     * 将路由配置转化为多条路由片段, 并返回路由条件和片段路径
     * return: Map<Expression, RouteUri>
     */
    public List<String> convert(ScatterGatherConfig config, ComponentModel component, BasicFlow flow) {
        List<ScatterGatherConfig.Branch> branches = config.getBranches();
        List<String> branchList = new ArrayList<>();

        for (int i = 0; i < branches.size(); i++) {
            val branch = branches.get(i);
            FlowFragment fragment = new FlowFragment();

            MessageModel fragmentLastOutModel = config.getInModel();
            List<ProcessorModel> pipeline = new ArrayList<>();
            for (ProcessorEntity processorEntity : branch.getPipeline()) {
                val processorModel = resourceBuilder.buildProcessorModel(processorEntity, fragmentLastOutModel, flow);
                fragmentLastOutModel = processorModel.getOutModel();
                pipeline.add(processorModel);
            }

            fragment.setId(flow.getId());
            fragment.setFragmentId(component.getId() + DELIMITER + i);
            fragment.setPipeline(pipeline);
            fragment.setInModel(config.getInModel());
            fragment.setOutModel(fragmentLastOutModel);


            DalaranRoute fragmentRoute = flowBuilder.buildFlowFragment(fragment);

            // 因为后面会有一个聚合处理, 所以一定要输出为 Object, 否则 Mapper 不好处理
            if (fragmentRoute.isSerializedBody()) {
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
