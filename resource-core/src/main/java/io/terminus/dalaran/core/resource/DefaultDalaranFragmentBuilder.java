package io.terminus.dalaran.core.resource;

import io.terminus.dalaran.core.flow.DalaranFragmentBuilder;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.component.ProcessorModel;
import io.terminus.dalaran.model.component.ProcessorRouteInfo;
import io.terminus.dalaran.model.flow.FlowFragment;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

public class DefaultDalaranFragmentBuilder implements DalaranFragmentBuilder {

    private final DalaranResourceBuilder dalaranResourceBuilder;

    public DefaultDalaranFragmentBuilder(DalaranResourceBuilder dalaranResourceBuilder) {
        this.dalaranResourceBuilder = dalaranResourceBuilder;
    }

    @Override
    public FlowFragment buildFlowFragment(List<ProcessorRouteInfo> pipelineEntityList, MessageModel inModel, MessageModel outModel, String flowId, String fragmentId, Boolean tracing) {

        MessageModel fragmentLastOutModel = outModel;
        List<ProcessorModel> pipeline = new ArrayList<>();
        for (ProcessorRouteInfo processorRouteInfo : pipelineEntityList) {
            val processorModel = dalaranResourceBuilder.buildProcessorModel(toEntity(processorRouteInfo), fragmentLastOutModel);
            fragmentLastOutModel = processorModel.getOutModel();
            pipeline.add(processorModel);
        }
        FlowFragment fragment = new FlowFragment();
        fragment.setId(flowId);
        fragment.setFragmentId(fragmentId);
        fragment.setPipeline(pipeline);
        fragment.setInModel(inModel);
        fragment.setOutModel(fragmentLastOutModel);
        fragment.setTracing(tracing);
        return fragment;
    }

    private ProcessorEntity toEntity(ProcessorRouteInfo routeInfo) {
        ProcessorEntity entity = new ProcessorEntity();
        entity.setId(routeInfo.getId());
        entity.setGroup(routeInfo.getGroup());
        entity.setVersion(routeInfo.getVersion());
        entity.setName(routeInfo.getName());
        entity.setType(routeInfo.getType());
        entity.setConfig(routeInfo.getConfig());
        return entity;
    }
}
