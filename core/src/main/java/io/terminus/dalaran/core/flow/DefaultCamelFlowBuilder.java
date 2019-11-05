package io.terminus.dalaran.core.flow;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.TracingType;
import io.terminus.dalaran.config.ComponentInfo;
import io.terminus.dalaran.config.DalaranConfigField;
import io.terminus.dalaran.config.ProcessorInfo;
import io.terminus.dalaran.config.TriggerInfo;
import io.terminus.dalaran.core.component.*;
import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.context.DalaranModelTypeContext;
import io.terminus.dalaran.core.log.DalaranTraceLogger;
import io.terminus.dalaran.core.log.DalaranTracer;
import io.terminus.dalaran.core.log.TracingErrorHandlerFactory;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.component.ProcessorModel;
import io.terminus.dalaran.model.flow.*;
import lombok.val;
import org.apache.camel.builder.Builder;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.terminus.dalaran.DalaranConstants.*;
import static io.terminus.dalaran.core.flow.DefaultFlowValidateMessages.FIELD_NOT_NULL;
import static io.terminus.dalaran.core.flow.DefaultFlowValidateMessages.MODEL_NOT_EQUALLY;
import static io.terminus.dalaran.core.flow.FlowSuggest.ADD_MAPPER;
import static io.terminus.dalaran.model.flow.ValidateMessageTarget.Processor;
import static io.terminus.dalaran.model.flow.ValidateMessageTarget.Trigger;
import static org.apache.camel.builder.Builder.constant;

public class DefaultCamelFlowBuilder implements DalaranFlowBuilder<DalaranRoute> {

    private final DalaranTraceLogger traceLogger;

    private final TracingErrorHandlerFactory errorHandlerFactory;

    private final DalaranModelTypeContext converterContext;

    private final DalaranComponentContext componentContext;

    public DefaultCamelFlowBuilder(
            DalaranTraceLogger traceLogger,
            TracingErrorHandlerFactory errorHandlerFactory,
            DalaranModelTypeContext converterContext,
            DalaranComponentContext componentContext
    ) {
        this.traceLogger = traceLogger;
        this.errorHandlerFactory = errorHandlerFactory;
        this.converterContext = converterContext;
        this.componentContext = componentContext;
    }

    @Override
    public DalaranRoute buildTriggerFlow(TriggerFlow flow) {
        val triggerComponent = componentContext.getTrigger(flow.getTriggerType());
        val triggerInfo = componentContext.getTriggerInfo(flow.getTriggerType());

        Object triggerConfig = flow.getTriggerConfig();
        if (triggerComponent instanceof DalaranTriggerFlowConfigCustomConverter) {
            triggerConfig = ((DalaranTriggerFlowConfigCustomConverter) triggerComponent).convert(triggerConfig, flow);
        }

        val route = createRouteDefinition();
        route.setId(flow.getRouteId());
        route.setProperty(TRACING_FLOW_ID).constant(flow.getId());
        triggerComponent.buildFromRoute(route, triggerConfig);

        String bodyType = triggerInfo.getModelType();
        if (UNKNOWN_MODEL_TYPE.equals(bodyType)) {
            if (flow.getInModel() == null) {
                bodyType = UNKNOWN_MODEL_TYPE;
            } else {
                bodyType = flow.getInModel().getModelType();
            }
        }
        buildFlowRoute(route, flow, TracingType.Flow, bodyType);
        return route;
    }

    @Override
    public DalaranRoute buildSubFLow(SubFlow flow) {
        val route = createRouteDefinition(flow);
        route.setProperty(TRACING_FLOW_ID).constant(flow.getId());
        if (flow.getInModel() == null) {
            buildFlowRoute(route, flow, TracingType.SubFlow, "UNKNOWN");
        } else {
            buildFlowRoute(route, flow, TracingType.SubFlow, flow.getInModel().getModelType());
        }
        return route;
    }

    @Override
    public DalaranRoute buildFlowFragment(FlowFragment fragment) {
        val route = createRouteDefinition(fragment);
        fragment.getProperties().forEach((key, value) -> route.setProperty(key, constant(value)));
        buildFlowRoute(route, fragment, null, fragment.getInModelType());
        return route;
    }

    @Override
    public DalaranRoute buildTestFLow(BasicFlow flow) {
        // enable tracing on test mode
        flow.setTracing(true);
        TracingType tracingType;
        if (flow instanceof SubFlow) {
            tracingType = TracingType.TestSubFlow;
        } else {
            tracingType = TracingType.TestFlow;
        }
        val route = createRouteDefinition();
        route.setId(TEST_FLOW_PREFIX + flow.getRouteId());
        route.setProperty(TRACING_FLOW_ID).constant(flow.getId());
        route.from(DalaranConstants.TEST_FLOW_DIRECT_PREFIX + flow.getRouteId());
        if (flow.getInModel() == null) {
            buildFlowRoute(route, flow, tracingType, "UNKNOWN");
        } else if (flow.getInModel().getModelType().equals("OBJECT")) {
            buildFlowRoute(route, flow, tracingType, "JSON");
        } else {
            buildFlowRoute(route, flow, tracingType, flow.getInModel().getModelType());
        }
        return route;
    }

    @Override
    public DalaranRoute buildTestSubFLow(SubFlow flow) {
        val route = createRouteDefinition();
        route.setId(TEST_SUB_FLOW_PREFIX + flow.getRouteId());
        route.setProperty(TRACING_FLOW_ID).constant(flow.getId());
        route.from(DalaranConstants.TEST_SUB_FLOW_DIRECT_PREFIX + flow.getRouteId());
        flow.setTracing(true);
        if (flow.getInModel() == null) {
            buildFlowRoute(route, flow, TracingType.TestSubFlow, "UNKNOWN");
        } else {
            buildFlowRoute(route, flow, TracingType.TestSubFlow, flow.getInModel().getModelType());
        }
        return route;
    }

    @Override
    public List<FlowValidation> validateFlow(BasicFlow flow) {
        MessageModel lastModel = flow.getInModel();
        List<FlowValidation> validateMessages = new ArrayList<>();
        // TODO 检查模型, 提示模型不匹配以及加入 Mapper 的建议
        for (ProcessorModel processorModel : flow.getPipeline()) {
            DalaranProcessor processor = componentContext.getProcessor(processorModel.getType());
            ProcessorInfo processorInfo = componentContext.getProcessorInfo(processorModel.getType());
            List<FlowValidation> processorMessageList = validate(processorInfo, processorModel.getConfig());
            if (processor instanceof DalaranComponentValidator) {
                List<FlowValidation> processorCustomMessageList = ((DalaranComponentValidator) processor).validate(processorModel.getConfig());
                processorMessageList.addAll(processorCustomMessageList);
            }
            if (lastModel != null && !lastModel.equals(processorModel.getInModel())) {
                FlowValidation message = FlowValidationBuilder.newBuilder()
                        .message(MODEL_NOT_EQUALLY).suggest(ADD_MAPPER).build();
                processorMessageList.add(message);
            }
            processorMessageList.forEach(message -> {
                message.setTargetType(Processor);
                message.setTargetId(processorModel.getId());
            });
            lastModel = processorModel.getOutModel();
            validateMessages.addAll(processorMessageList);
        }
        if (flow instanceof TriggerFlow) {
            TriggerFlow triggerFlow = ((TriggerFlow) flow);
            DalaranTrigger trigger = componentContext.getTrigger(triggerFlow.getTriggerType());
            TriggerInfo triggerInfo = componentContext.getTriggerInfo(triggerFlow.getTriggerType());

            List<FlowValidation> flowValidateMessages = validate(triggerInfo, triggerFlow.getTriggerConfig());
            if (trigger instanceof DalaranComponentValidator) {
                List<FlowValidation> triggerCustomMessageList = ((DalaranComponentValidator) trigger).validate(triggerFlow.getTriggerConfig());
                flowValidateMessages.addAll(triggerCustomMessageList);
            }
            flowValidateMessages.forEach(message -> message.setTargetType(Trigger));
            validateMessages.addAll(flowValidateMessages);
        }
        if (lastModel != null && flow.getOutModel() != null && !lastModel.equals(flow.getOutModel())) {
            FlowValidation message = FlowValidationBuilder.newBuilder()
                    .flowEnd().message(MODEL_NOT_EQUALLY).suggest(ADD_MAPPER).build();
            validateMessages.add(message);
        }
        return validateMessages;
    }

    private List<FlowValidation> validate(ComponentInfo componentInfo, Object config) {
        List<FlowValidation> validateMessages = new ArrayList<>();
        for (DalaranConfigField configField : componentInfo.getConfigFields()) {
            try {
                if (configField.isRequired() && StringUtils.isBlank(BeanUtils.getProperty(config, configField.getName()))) {
                    FlowValidation message = FlowValidationBuilder.newBuilder()
                            .field(configField.getName()).message(FIELD_NOT_NULL).build();
                    validateMessages.add(message);
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return validateMessages;
    }

    private String convertModel(DalaranRoute route, String currentBodyType, String nextBodyType, MessageModel currentModel, MessageModel nextModel) {
        // 如果组件声明类型为 Unknown, 则尝试取模型的类型
        if (UNKNOWN_MODEL_TYPE.equalsIgnoreCase(currentBodyType) && currentModel != null) {
            currentBodyType = currentModel.getModelType();
        }
        if (!UNKNOWN_MODEL_TYPE.equalsIgnoreCase(currentBodyType) && !nextBodyType.equals(currentBodyType)) {
            // 如果下一个组件声明类型为 Unknown, 则尝试取下一个模型的类型
            if (UNKNOWN_MODEL_TYPE.equalsIgnoreCase(nextBodyType) && nextModel != null) {
                nextBodyType = nextModel.getModelType();
            }
            // 如果目前是 Object, 则从 Object 转至目标类型
            if (DalaranConstants.OBJECT_MODEL_TYPE.equalsIgnoreCase(currentBodyType)) {
                converterContext.fromObject(route, currentModel, nextBodyType);
                // 如果目标类型是 Object, 则从当前类型转至 Object
            } else if (DalaranConstants.OBJECT_MODEL_TYPE.equalsIgnoreCase(nextBodyType)) {
                converterContext.toObject(route, currentBodyType);
                // 如果都不是, 则从当前类型转至 Object, 再由 Object 转至目标类型
            } else {
                converterContext.toObject(route, currentBodyType);
                converterContext.fromObject(route, currentModel, nextBodyType);
            }
        }
        return nextBodyType;
    }

    // TODO 这里还是比较乱的...
    private void buildFlowRoute(DalaranRoute route, BasicFlow flow, TracingType flowTracingType, @NotNull String currentBodyType) {
        Map context = new HashMap<>();
        route.setProperty(DALARAN_CONTEXT_EXCHANGE, Builder.constant(context));
        DalaranTracer flowTracer = null;
        if (flow.isTracing() && flowTracingType != null) {
            flowTracer = DalaranTracer.buildTracer(traceLogger, flowTracingType);
        }
        if (flowTracer != null) {
            flowTracer.before(route, currentBodyType);
        }
        List<ProcessorModel> processorList = flow.getPipeline();
        MessageModel currentModel = flow.getInModel();
        ProcessorInfo currentProcessorInfo;
        for (ProcessorModel processor : processorList) {
            DalaranTracer spanTracer = null;
            if (flow.isTracing()) {
                spanTracer = DalaranTracer.buildFlowSpanTracer(traceLogger, processor.getId());
            }
            DalaranProcessor processorComponent = componentContext.getProcessor(processor.getType());
            currentProcessorInfo = componentContext.getProcessorInfo(processor.getType());

            if (spanTracer != null) {
                spanTracer.before(route, currentBodyType);
            }
            // TODO 这里还是比较奇怪, 有点绕, 而且有些特殊场景没有考虑到
//                currentBodyIsSerialized = currentModel.getModelType().isSerialized();
            boolean needConvert = true;
            if (processorComponent instanceof DalaranMessageBodyCustomConverter) {
                needConvert = ((DalaranMessageBodyCustomConverter) processorComponent).customBodyConvert(route, processor.getConfig(), currentBodyType);
            }
            String nextBodyType = currentProcessorInfo.getModelType();
            MessageModel nextModel = processor.getInModel();
            if (needConvert) {
                convertModel(route, currentBodyType, nextBodyType, currentModel, nextModel);
            }
            currentBodyType = nextBodyType;

            Object config = processor.getConfig();

            if (processorComponent instanceof DalaranProcessorConfigCustomConverter) {
                config = ((DalaranProcessorConfigCustomConverter) processorComponent).convert(config, processor, flow);
            }

            processorComponent.configure(route, config);

            nextModel = processor.getOutModel();

            if (nextModel != null) {
                currentModel = nextModel;
            }
            if (spanTracer != null) {
                if (currentModel == null) {
                    spanTracer.after(route);
                } else {
                    spanTracer.after(route, currentModel.getModelType());
                }
            }
        }

        if (flowTracer != null) {
            flowTracer.after(route, currentBodyType);
        }

        if (!UNKNOWN_MODEL_TYPE.equalsIgnoreCase(currentBodyType) && flow.getOutModel() != null) {
            String nextBodyType = flow.getOutModel().getModelType();
            if (!nextBodyType.equals(currentBodyType)) {
                if (DalaranConstants.OBJECT_MODEL_TYPE.equalsIgnoreCase(currentBodyType)) {
                    converterContext.fromObject(route, currentModel, nextBodyType);
                } else if (DalaranConstants.OBJECT_MODEL_TYPE.equalsIgnoreCase(nextBodyType)) {
                    converterContext.toObject(route, currentBodyType);
                } else {
                    converterContext.toObject(route, currentBodyType);
                    converterContext.fromObject(route, currentModel, nextBodyType);
                }
            }
            currentBodyType = nextBodyType;
            currentModel = flow.getOutModel();
        }

        route.setLastBodyType(currentBodyType);
        route.setLastOutModel(currentModel);
    }

    private DalaranRoute createRouteDefinition(BasicFlow flow) {
        val route = createRouteDefinition();
        route.setId(flow.getRouteId());
        route.from(flow.getDirectRouteUri());
        return route;
    }

    private DalaranRoute createRouteDefinition() {
        DalaranRoute route = new DalaranRoute();
        route.errorHandler(errorHandlerFactory);
        return route;
    }
}
