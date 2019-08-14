package io.terminus.dalaran.core.flow;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.core.component.*;
import io.terminus.dalaran.core.config.ComponentInfo;
import io.terminus.dalaran.core.config.DalaranConfigField;
import io.terminus.dalaran.core.config.ProcessorInfo;
import io.terminus.dalaran.core.config.TriggerInfo;
import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.context.DalaranConverterContext;
import io.terminus.dalaran.core.log.DalaranTraceLogger;
import io.terminus.dalaran.core.log.DalaranTracer;
import io.terminus.dalaran.core.log.TracingErrorHandlerFactory;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.component.ProcessorModel;
import io.terminus.dalaran.model.flow.*;
import lombok.val;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static io.terminus.dalaran.DalaranConstants.TEST_FLOW_PREFIX;
import static io.terminus.dalaran.DalaranConstants.TEST_SUB_FLOW_PREFIX;
import static io.terminus.dalaran.core.flow.DefaultFlowValidateMessages.FIELD_NOT_NULL;
import static io.terminus.dalaran.core.flow.DefaultFlowValidateMessages.MODEL_NOT_EQUALLY;
import static io.terminus.dalaran.core.flow.FlowSuggest.ADD_MAPPER;
import static io.terminus.dalaran.model.flow.ValidateMessageTarget.Processor;
import static io.terminus.dalaran.model.flow.ValidateMessageTarget.Trigger;

public class DefaultCamelFlowBuilder implements DalaranFlowBuilder<DalaranRoute> {

    private final DalaranTraceLogger traceLogger;

    private final TracingErrorHandlerFactory errorHandlerFactory;

    private final DalaranConverterContext converterContext;

    private final DalaranComponentContext componentContext;

    public DefaultCamelFlowBuilder(
            DalaranTraceLogger traceLogger,
            TracingErrorHandlerFactory errorHandlerFactory,
            DalaranConverterContext converterContext,
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

        DalaranTracer flowTracer = null;
        if (flow.isTracing()) {
            flowTracer = DalaranTracer.buildFlowTracer(traceLogger, flow.getId());
        }
        DalaranTrigger triggerBean = componentContext.getTrigger(flow.getTriggerType());
        Object triggerConfig = flow.getTriggerConfig();
        if (triggerBean instanceof DalaranTriggerFlowConfigCustomConverter) {
            triggerConfig = ((DalaranTriggerFlowConfigCustomConverter) triggerBean).convert(triggerConfig, flow);
        }

        val route = createRouteDefinition();
        route.setId(flow.getRouteId());
        triggerComponent.buildFromRoute(route, triggerConfig);
        if (flowTracer != null) {
            flowTracer.before(route, flow.getInModel().getModelType());
        }

        buildFlowRoute(route, flow, null);
        // TODO 流程最后不可得知触发器的出模型, 所以无法判断做格式转换, 最保险的方式是固定转为 Object, 在 trigger 端在根据要求做一次序列化, 但是会有性能损耗
        // TODO 另外这里也不好判断是否是最后的节点, 因为存在分支, 暂时将最后节点作为流输出节点
        // TODO 也可以考虑加一个动态节点, 根据上下文判断如何做处理, 这样就没办法用 camel DSL 了
        if (flowTracer != null) {
            flowTracer.after(route, flow.getOutModel().getModelType());
        }
        return route;
    }

    @Override
    public DalaranRoute buildSubFLow(SubFlow flow) {
        val flowTracer = DalaranTracer.buildSubFlowTracer(traceLogger, flow.getId());
        val route = createRouteDefinition(flow);
        if (flow.getInModel() == null) {
            flowTracer.before(route);
        } else {
            flowTracer.before(route, flow.getInModel().getModelType());
        }

        buildFlowRoute(route, flow, null);

        if (flow.getOutModel() == null) {
            flowTracer.after(route);
        } else {
            flowTracer.after(route, flow.getOutModel().getModelType());
        }
        return route;
    }

    @Override
    public DalaranRoute buildFlowFragment(FlowFragment fragment) {
        val route = createRouteDefinition(fragment);
        buildFlowRoute(route, fragment, false);
        return route;
    }

    @Override
    public DalaranRoute buildTestFLow(BasicFlow flow) {
        val flowTracer = DalaranTracer.buildTestFlowTracer(traceLogger, flow.getId());
        val route = createRouteDefinition();
        route.setId(TEST_FLOW_PREFIX + flow.getRouteId());
        route.from(DalaranConstants.TEST_FLOW_DIRECT_PREFIX + flow.getRouteId());
        if (flow.getInModel() == null) {
            flowTracer.before(route);
        } else {
            flowTracer.before(route, flow.getInModel().getModelType());
        }

        // TODO 测试的输入一定是序列化的, XML/Json 等都是直接扔进去, 如果入参是 Object, 前端引导输入 Json 做反序列化处理吧
//        if (!flow.getInModel().getModelType().isSerialized()) {
//            route.process(exchange -> {
//                String bodyString = exchange.getIn().getBody(String.class);
//                InputStream input = new ByteArrayInputStream(bodyString.getBytes());
//                exchange.getOut().setBody(input);
//            });
//            converterContext.toObject(route, BodyType.JSON);
//        }

        // enable tracing on test mode
        flow.setTracing(true);

        buildFlowRoute(route, flow, true);

        if (flow.getOutModel() == null) {
            flowTracer.after(route);
        } else {
            flowTracer.after(route, flow.getOutModel().getModelType());
        }

        return route;
    }

    @Override
    public DalaranRoute buildTestSubFLow(SubFlow flow) {
        val flowTracer = DalaranTracer.buildTestFlowTracer(traceLogger, flow.getId());
        val route = createRouteDefinition();
        route.setId(TEST_SUB_FLOW_PREFIX + flow.getRouteId());
        route.from(DalaranConstants.TEST_SUB_FLOW_DIRECT_PREFIX + flow.getRouteId());
        flowTracer.before(route, flow.getInModel().getModelType());
        flow.setTracing(true);
        buildFlowRoute(route, flow, true);
        flowTracer.after(route, flow.getOutModel().getModelType());
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
        if (lastModel != null && !lastModel.equals(flow.getOutModel())) {
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

    // TODO currentBodyIsSerialized 这个还是比较绕的....
    private void buildFlowRoute(DalaranRoute route, BasicFlow flow, Boolean currentBodyIsSerialized) {
        List<ProcessorModel> processorList = flow.getPipeline();
        MessageModel currentModel = flow.getInModel();
        // TODO in model maybe null
        if (currentBodyIsSerialized == null) {
            if (currentModel != null) {
                currentBodyIsSerialized = currentModel.getModelType().isSerialized();
            } else {
                currentBodyIsSerialized = true;
            }
        }
        ProcessorInfo currentProcessorInfo = null;
        for (ProcessorModel processor : processorList) {
            DalaranTracer spanTracer = null;
            if (flow.isTracing()) {
                spanTracer = DalaranTracer.buildFlowSpanTracer(traceLogger, flow.getId(), processor.getId());
            }
            DalaranProcessor processorComponent = componentContext.getProcessor(processor.getType());
            currentProcessorInfo = componentContext.getProcessorInfo(processor.getType());

            // TODO no model
            if (spanTracer != null && currentModel != null) {
                spanTracer.before(route, currentModel.getModelType());
            }
            // TODO 这里还是比较奇怪, 有点绕, 而且有些特殊场景没有考虑到
//                currentBodyIsSerialized = currentModel.getModelType().isSerialized();
            boolean needConvert = true;
            if (processorComponent instanceof DalaranMessageBodyCustomConverter) {
                needConvert = ((DalaranMessageBodyCustomConverter) processorComponent).customBodyConvert(route, processor.getConfig(), currentBodyIsSerialized);
            }
            if (currentModel != null && needConvert && currentProcessorInfo.getInputSerializeType() != BodySerializeType.All) {
                // TODO convert tracing, 暂时没必要, 先注掉吧, 影响性能
                // TODO processor 的输入和输出一定是一种类型
//                DalaranTracer convertTracer = DalaranTracer.buildConvertTracer(traceLogger, flow.getId(), processor.getId());
                if (currentProcessorInfo.getInputSerializeType() == BodySerializeType.Serialized && !currentBodyIsSerialized) {
//                    convertTracer.before(route, BodyType.OBJECT);
                    converterContext.fromObject(route, currentModel);
                    currentBodyIsSerialized = true;
//                    convertTracer.after(route, currentModel.getModelType());
                } else if (currentProcessorInfo.getInputSerializeType() == BodySerializeType.Object && currentBodyIsSerialized) {
//                    convertTracer.before(route, currentModel.getModelType());
                    converterContext.toObject(route, currentModel);
                    currentBodyIsSerialized = false;
//                    convertTracer.after(route, BodyType.OBJECT);
                }
            }
            Object config = processor.getConfig();

            if (processorComponent instanceof DalaranProcessorConfigCustomConverter) {
                config = ((DalaranProcessorConfigCustomConverter) processorComponent).convert(config, processor, flow);
            }

            processorComponent.configure(route, config);

            MessageModel outModel = processor.getOutModel();

            switch (currentProcessorInfo.getOutputSerializeType()) {
                case Serialized:
                    currentBodyIsSerialized = true;
                    break;
                case Object:
                    currentBodyIsSerialized = false;
                    break;
            }

            if (outModel != null) {
                currentModel = outModel;
            }
            if (spanTracer != null && currentModel != null) {
                spanTracer.after(route, currentModel.getModelType());
            }
        }

        MessageModel outModel = flow.getOutModel();
        // TODO SubFlow / Router 等 Processor 不需要做转化, 因为在片段里已经做过了, 而且只能在里面做, 但是形式有点丑, 回头看看怎么优化
        if (currentProcessorInfo != null && currentProcessorInfo.getOutputSerializeType() != BodySerializeType.All) {
//            DalaranTracer convertTracer = DalaranTracer.buildConvertTracer(traceLogger, flow.getId(), lastProcessor.getId());
            if (outModel != null && outModel.getModelType().isSerialized() != currentBodyIsSerialized) {
//            DalaranTracer convertTracer = DalaranTracer.buildConvertTracer(traceLogger, flow.getId(), lastProcessor.getId());
                if (outModel.getModelType().isSerialized()) {
//                convertTracer.before(route, BodyType.OBJECT);
                    converterContext.fromObject(route, outModel);
                    currentBodyIsSerialized = true;
//                convertTracer.after(route, currentModel.getModelType());
                } else {
//                convertTracer.before(route, currentModel.getModelType());
                    converterContext.toObject(route, currentModel);
                    currentBodyIsSerialized = false;
//                convertTracer.after(route, BodyType.OBJECT);
                }
            }
        }

        route.setSerializedBody(currentBodyIsSerialized);
        route.setLastOutModel(outModel);
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
