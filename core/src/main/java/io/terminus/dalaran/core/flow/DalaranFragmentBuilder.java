package io.terminus.dalaran.core.flow;

import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.component.ProcessorRouteInfo;
import io.terminus.dalaran.model.flow.FlowFragment;

import java.util.List;

public interface DalaranFragmentBuilder {

    FlowFragment buildFlowFragment(List<ProcessorRouteInfo> pipelineEntityList, MessageModel inModel, MessageModel outModel, Long flowId, String fragmentId, Boolean tracing);
}
