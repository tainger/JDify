package io.terminus.dalaran.support.flow;

import io.terminus.dalaran.*;
import io.terminus.dalaran.config.OutModelConfig;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ProcessorModel;
import io.terminus.dalaran.model.config.ProcessorInfo;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowFragment;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;
import io.terminus.dalaran.support.trace.DalaranTracer;
import io.terminus.dalaran.support.trace.TracingErrorHandlerFactory;
import lombok.val;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

import static io.terminus.dalaran.DalaranConstants.*;

public class FlowBuilder {

    private final DalaranConverterContext converterContext;

    private final DalaranComponentContext componentContext;

    private final DalaranTraceLogger traceLogger;

    private final TracingErrorHandlerFactory errorHandlerFactory;

    public FlowBuilder(
            DalaranConverterContext converterContext,
            DalaranComponentContext componentContext,
            DalaranTraceLogger traceLogger,
            TracingErrorHandlerFactory errorHandlerFactory
    ) {
        this.converterContext = converterContext;
        this.componentContext = componentContext;
        this.traceLogger = traceLogger;
        this.errorHandlerFactory = errorHandlerFactory;
    }

    RouteDefinition buildTriggerFlow(TriggerFlow flow) {
        val triggerComponent = componentContext.getTrigger(flow.getTriggerType());
        val flowTracer = DalaranTracer.buildFlowTracer(traceLogger, flow.getId());
        val route = newRouteDefinition();
        route.setId(flow.getRouteId());
        triggerComponent.buildFromRoute(route, flow.getTriggerConfig());
        flowTracer.before(route, flow.getInModel().getModelType());

        buildFlowRoute(route, flow);
        // TODO 流程最后不可得知触发器的出模型, 所以无法判断做格式转换, 最保险的方式是固定转为 Object, 在 trigger 端在根据要求做一次序列化, 但是会有性能损耗
        // TODO 另外这里也不好判断是否是最后的节点, 因为存在分支, 暂时将最后节点作为流输出节点
        // TODO 也可以考虑加一个动态节点, 根据上下文判断如何做处理, 这样就没办法用 camel DSL 了
        flowTracer.after(route, flow.getOutModel().getModelType());
        return route;
    }

    RouteDefinition buildSubFLow(SubFlow flow) {
        val flowTracer = DalaranTracer.buildSubFlowTracer(traceLogger, flow.getId());
        val route = newRouteDefinition();
        route.setId(flow.getRouteId());
        route.from(DIRECT_PREFIX + flow.getRouteId());
        flowTracer.before(route, flow.getInModel().getModelType());
        buildFlowRoute(route, flow);
        flowTracer.after(route, flow.getOutModel().getModelType());
        return route;
    }

    RouteDefinition buildFlowFragment(FlowFragment fragment) {
        val route = newRouteDefinition();
        route.setId(fragment.getRouteId());
        route.from(DIRECT_PREFIX + fragment.getRouteId());
        buildFlowRoute(route, fragment);
        return route;
    }

    RouteDefinition buildTestFLow(BasicFlow flow) {
        val flowTracer = DalaranTracer.buildTestFlowTracer(traceLogger, flow.getId());
        val route = newRouteDefinition();
        route.setId(flow.getRouteId());
        route.from(TEST_FLOW_DIRECT_PREFIX + flow.getRouteId());

        // TODO 测试的输入一定是序列化的, XML/Json 等都是直接扔进去, 如果入参是 Object, 前端引导输入 Json 做反序列化处理吧
        if (!flow.getInModel().getModelType().isSerialized()) {
            converterContext.toObject(route, BodyType.JSON);
        }
        flowTracer.before(route, flow.getInModel().getModelType());

        buildFlowRoute(route, flow);

        flowTracer.after(route, flow.getOutModel().getModelType());
        return route;
    }

    private void buildFlowRoute(RouteDefinition route, BasicFlow flow) {
        List<ProcessorModel> processorList = flow.getPipeline();
        // TODO in model maybe null
        MessageModel currentModel = flow.getInModel();
        boolean currentBodyIsSerialized = currentModel.getModelType().isSerialized();

        for (ProcessorModel processor : processorList) {
            DalaranTracer spanTracer = DalaranTracer.buildFlowSpanTracer(traceLogger, flow.getId(), processor.getId());
            DalaranProcessor processorComponent = componentContext.getProcessor(processor.getType());
            ProcessorInfo processorInfo = componentContext.getProcessorInfo(processor.getType());

            // TODO no model
            spanTracer.before(route, currentModel.getModelType());
            // TODO 这里还是比较奇怪, 有点绕, 而且有些特殊场景没有考虑到
            if (currentModel != null) {
//                currentBodyIsSerialized = currentModel.getModelType().isSerialized();
                boolean needConvert = true;
                if (processorComponent instanceof CustomConvert) {
                    needConvert = ((CustomConvert) processorComponent).customConvert(route, processor.getConfig(), currentBodyIsSerialized);
                }
                if (needConvert && processorInfo.isSerializedBody() != currentBodyIsSerialized) {
                    // TODO convert tracing, 暂时没必要, 先注掉吧, 影响性能
//                DalaranTracer convertTracer = DalaranTracer.buildConvertTracer(traceLogger, flow.getId(), processor.getId());
                    if (processorInfo.isSerializedBody()) {
//                    convertTracer.before(route, BodyType.OBJECT);
                        converterContext.fromObject(route, currentModel);
//                    convertTracer.after(route, currentModel.getModelType());
                    } else {
//                    convertTracer.before(route, currentModel.getModelType());
                        converterContext.toObject(route, currentModel);
//                    convertTracer.after(route, BodyType.OBJECT);
                    }
                }
            }
            // TODO processor 的输入和输出一定是一种类型
            currentBodyIsSerialized = processorInfo.isSerializedBody();
            Object config = processor.getConfig();

            processorComponent.configure(route, config);

            MessageModel outModel = getProcessorOutModel(processor);

            if (outModel != null) {
                currentModel = outModel;
            }
            spanTracer.after(route, currentModel.getModelType());
        }

        MessageModel outModel = flow.getOutModel();
        if (outModel != null && outModel.getModelType().isSerialized() != currentBodyIsSerialized) {
//            DalaranTracer convertTracer = DalaranTracer.buildConvertTracer(traceLogger, flow.getId(), lastProcessor.getId());
            if (outModel.getModelType().isSerialized()) {
//                convertTracer.before(route, BodyType.OBJECT);
                converterContext.fromObject(route, currentModel);
//                convertTracer.after(route, currentModel.getModelType());
            } else {
//                convertTracer.before(route, currentModel.getModelType());
                converterContext.toObject(route, currentModel);
//                convertTracer.after(route, BodyType.OBJECT);
            }
        }
    }

    private MessageModel getProcessorOutModel(ProcessorModel processor) {
        if (processor.getConfig() instanceof OutModelConfig) {
            return ((OutModelConfig) processor.getConfig()).getOutModel();
        }
        return null;
    }

    private RouteDefinition newRouteDefinition() {
        return new RouteDefinition().errorHandler(errorHandlerFactory);
    }
}
