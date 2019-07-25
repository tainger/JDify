package io.terminus.dalaran.component.processor.foreach;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranProcessorConfigCustomConverter;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.flow.DalaranRoute;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.component.ComponentModel;
import io.terminus.dalaran.model.component.ProcessorModel;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowFragment;
import lombok.val;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.apache.camel.builder.Builder.body;

@Processor(
        value = "foreach",
        name = "数组循环",
        order = 20,
        configType = ForEachConfig.class
)
public class ForEachProcessor implements DalaranProcessor<String>, DalaranProcessorConfigCustomConverter<ForEachConfig, String> {

    @Autowired
    private DalaranContext<DalaranRoute> dalaranContext;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Override
    public void configure(ProcessorDefinition route, String fragmentUri) {
        route.split(body()).to(fragmentUri);
    }

    @Override
    public String convert(ForEachConfig config, ComponentModel component, BasicFlow flow) {
        MessageModel fragmentLastOutModel = component.getOutModel();
        List<ProcessorModel> pipeline = new ArrayList<>();
        for (ProcessorEntity processorEntity : config.getPipeline()) {
            val processorModel = resourceBuilder.buildProcessorModel(processorEntity, fragmentLastOutModel, flow);
            fragmentLastOutModel = processorModel.getOutModel();
            pipeline.add(processorModel);
        }

        FlowFragment fragment = new FlowFragment();
        fragment.setId(flow.getId());
        fragment.setFragmentId(component.getId());
        fragment.setPipeline(pipeline);
        fragment.setInModel(component.getInModel());
        fragment.setOutModel(fragmentLastOutModel);

        dalaranContext.addFragmentFlow(fragment);

        return fragment.getDirectRouteUri();
    }
}