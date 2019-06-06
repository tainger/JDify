package io.terminus.dalaran.core.flow;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranMessageBodyCustomConverter;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.model.ProcessorModel;
import io.terminus.dalaran.core.config.ProcessorInfo;
import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.context.DalaranConverterContext;
import io.terminus.dalaran.core.flow.model.BasicFlow;
import io.terminus.dalaran.core.flow.model.FlowFragment;
import io.terminus.dalaran.core.flow.model.SubFlow;
import io.terminus.dalaran.core.flow.model.TriggerFlow;
import io.terminus.dalaran.core.log.DalaranTraceLogger;
import io.terminus.dalaran.core.log.DalaranTracer;
import io.terminus.dalaran.core.log.TracingErrorHandlerFactory;
import io.terminus.dalaran.core.model.BodyType;
import io.terminus.dalaran.core.model.MessageModel;
import lombok.val;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static io.terminus.dalaran.core.DalaranConstants.DIRECT_PREFIX;
import static io.terminus.dalaran.core.DalaranConstants.TEST_FLOW_DIRECT_PREFIX;

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
        val flowTracer = DalaranTracer.buildFlowTracer(traceLogger, flow.getId());
        val route = newRouteDefinition();
        route.setId(flow.getRouteId());
        triggerComponent.buildFromRoute(route, flow.getTriggerConfig());
        flowTracer.before(route, flow.getInModel().getModelType());

        buildFlowRoute(route, flow, null);
        // TODO 流程最后不可得知触发器的出模型, 所以无法判断做格式转换, 最保险的方式是固定转为 Object, 在 trigger 端在根据要求做一次序列化, 但是会有性能损耗
        // TODO 另外这里也不好判断是否是最后的节点, 因为存在分支, 暂时将最后节点作为流输出节点
        // TODO 也可以考虑加一个动态节点, 根据上下文判断如何做处理, 这样就没办法用 camel DSL 了
        flowTracer.after(route, flow.getOutModel().getModelType());
        return route;
    }

    @Override
    public DalaranRoute buildSubFLow(SubFlow flow) {
        val flowTracer = DalaranTracer.buildSubFlowTracer(traceLogger, flow.getId());
        val route = newRouteDefinition();
        route.setId(flow.getRouteId());
        route.from(DIRECT_PREFIX + flow.getRouteId());
        flowTracer.before(route, flow.getInModel().getModelType());
        buildFlowRoute(route, flow, null);
        flowTracer.after(route, flow.getOutModel().getModelType());
        return route;
    }

    @Override
    public DalaranRoute buildFlowFragment(FlowFragment fragment) {
        val route = newRouteDefinition();
        route.setId(fragment.getRouteId());
        route.from(DIRECT_PREFIX + fragment.getRouteId());

        buildFlowRoute(route, fragment, false);
        return route;
    }

    @Override
    public DalaranRoute buildTestFLow(BasicFlow flow) {
        val flowTracer = DalaranTracer.buildTestFlowTracer(traceLogger, flow.getId());
        val route = newRouteDefinition();
        route.setId(flow.getRouteId());
        route.from(TEST_FLOW_DIRECT_PREFIX + flow.getRouteId());
        flowTracer.before(route, flow.getInModel().getModelType());

        // TODO 测试的输入一定是序列化的, XML/Json 等都是直接扔进去, 如果入参是 Object, 前端引导输入 Json 做反序列化处理吧
        if (!flow.getInModel().getModelType().isSerialized()) {
            route.process(exchange -> {
                String bodyString = exchange.getIn().getBody(String.class);
                InputStream input = new ByteArrayInputStream(bodyString.getBytes());
                exchange.getOut().setBody(input);
            });
            converterContext.toObject(route, BodyType.JSON);
        }

        buildFlowRoute(route, flow, false);

        flowTracer.after(route, flow.getOutModel().getModelType());
        return route;
    }

    // TODO currentBodyIsSerialized 这个还是比较绕的....
    private void buildFlowRoute(DalaranRoute route, BasicFlow flow, Boolean currentBodyIsSerialized) {
        List<ProcessorModel> processorList = flow.getPipeline();
        MessageModel currentModel = flow.getInModel();
        // TODO in model maybe null
        if (currentBodyIsSerialized == null && currentModel != null) {
            currentBodyIsSerialized = currentModel.getModelType().isSerialized();
        }
        ProcessorInfo currentProcessorInfo = null;
        for (ProcessorModel processor : processorList) {
            DalaranTracer spanTracer = DalaranTracer.buildFlowSpanTracer(traceLogger, flow.getId(), processor.getId());
            DalaranProcessor processorComponent = componentContext.getProcessor(processor.getType());
            currentProcessorInfo = componentContext.getProcessorInfo(processor.getType());

            // TODO no model
            spanTracer.before(route, currentModel.getModelType());
            // TODO 这里还是比较奇怪, 有点绕, 而且有些特殊场景没有考虑到
//                currentBodyIsSerialized = currentModel.getModelType().isSerialized();
            boolean needConvert = true;
            if (processorComponent instanceof DalaranMessageBodyCustomConverter) {
                needConvert = ((DalaranMessageBodyCustomConverter) processorComponent).customBodyConvert(route, processor.getConfig(), currentBodyIsSerialized);
            }
            if (needConvert && currentProcessorInfo.getInputSerializeType() != BodySerializeType.All) {
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
            spanTracer.after(route, currentModel.getModelType());
        }

        MessageModel outModel = flow.getOutModel();
        // TODO SubFlow / Router 等 Processor 不需要做转化, 因为在片段里已经做过了, 而且只能在里面做, 但是形式有点丑, 回头看看怎么优化
        if (currentProcessorInfo.getOutputSerializeType() != BodySerializeType.All) {
//            DalaranTracer convertTracer = DalaranTracer.buildConvertTracer(traceLogger, flow.getId(), lastProcessor.getId());
            if (outModel != null && outModel.getModelType().isSerialized() != currentBodyIsSerialized) {
//            DalaranTracer convertTracer = DalaranTracer.buildConvertTracer(traceLogger, flow.getId(), lastProcessor.getId());
                if (outModel.getModelType().isSerialized()) {
//                convertTracer.before(route, BodyType.OBJECT);
                    converterContext.fromObject(route, currentModel);
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

    private DalaranRoute newRouteDefinition() {
        DalaranRoute route = new DalaranRoute();
        route.errorHandler(errorHandlerFactory);
        return route;
    }
}
