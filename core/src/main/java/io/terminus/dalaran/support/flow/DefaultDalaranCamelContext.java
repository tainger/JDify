package io.terminus.dalaran.support.flow;

import io.terminus.dalaran.*;
import io.terminus.dalaran.model.DalaranFlow;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ProcessorModel;
import io.terminus.dalaran.model.config.ProcessorInfo;
import io.terminus.dalaran.support.trace.DalaranTracer;
import io.terminus.dalaran.support.trace.TracingErrorHandlerFactory;
import lombok.val;
import org.apache.camel.CamelContext;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.Builder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

import static io.terminus.dalaran.DalaranConstants.*;

public class DefaultDalaranCamelContext implements DalaranContext {

    private final CamelContext camelContext;

    private final DalaranConverterContext converterContext;

    private final DalaranComponentContext componentContext;

    private final TracingErrorHandlerFactory errorHandlerFactory;

    private final DalaranTraceLogger traceLogger;

    public DefaultDalaranCamelContext(DalaranConverterContext converterContext, DalaranComponentContext componentContext, DalaranTraceLogger traceLogger, TracingErrorHandlerFactory errorHandlerFactory) {
        this.converterContext = converterContext;
        this.componentContext = componentContext;
        this.traceLogger = traceLogger;
        this.camelContext = new DefaultCamelContext();
        try {
            camelContext.setTracing(true);
            camelContext.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.errorHandlerFactory = errorHandlerFactory;
    }

    @Override
    public void removeFlow(Long flowId) {
        try {
            camelContext.removeRoute(FLOW_CAMEL_URI_PREFIX + flowId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeFlows(List<Long> flowIds) {
        flowIds.forEach(this::removeFlow);
    }

    @Override
    public void removeAllFlow() throws Exception {
        camelContext.removeRouteDefinitions(camelContext.getRouteDefinitions());
    }

    // TODO Flow 可以抽象一个 Builder, 但暂时没有必要
    @Override
    public void addFlow(DalaranFlow dalaranFlow) {
        try {
            camelContext.removeRoute(FLOW_PREFIX + dalaranFlow.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        val triggerComponent = componentContext.getTrigger(dalaranFlow.getTriggerType());
        val flowTracer = DalaranTracer.buildFlowTracer(traceLogger, dalaranFlow.getId());

        val route = new RouteDefinition();
        route.setId(FLOW_PREFIX + dalaranFlow.getId());
        route.errorHandler(errorHandlerFactory);
        triggerComponent.buildFromRoute(route, dalaranFlow.getTriggerConfig());
        flowTracer.before(route, dalaranFlow.getInModel().getModelType());

        buildFlowRoute(route, dalaranFlow);
        // TODO 流程最后不可得知触发器的出模型, 所以无法判断做格式转换, 最保险的方式是固定转为 Object, 在 trigger 端在根据要求做一次序列化, 但是会有性能损耗
        // TODO 另外这里也不好判断是否是最后的节点, 因为存在分支, 暂时将最后节点作为流输出节点
        // TODO 也可以考虑加一个动态节点, 根据上下文判断如何做处理, 这样就没办法用 camel DSL 了
        flowTracer.after(route, dalaranFlow.getOutModel().getModelType());


        // TODO on exception...
        try {
            camelContext.addRouteDefinition(route);
        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }

    public void addFlows(List<DalaranFlow> flows) {
        flows.forEach(this::addFlow);
    }

    @Override
    public void addTestFlow(DalaranFlow dalaranFlow) {
        addFlow(dalaranFlow);

        String routeId = TEST_FLOW_PREFIX + dalaranFlow.getId();
        try {
            camelContext.removeRoute(routeId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        val tracer = DalaranTracer.buildTestFlowTracer(traceLogger, dalaranFlow.getId());

        val route = new RouteDefinition();
        route.errorHandler(errorHandlerFactory);
        route.setProperty(TEST_FLOW, Builder.constant(Boolean.TRUE));
        route.setId(routeId);
        route.from(TEST_FLOW_CAMEL_URI_PREFIX + dalaranFlow.getId());
        tracer.before(route, dalaranFlow.getInModel().getModelType());
        // TODO 这里要判断的是流和触发器的 body 类型, 默认测试进来一定是 Serialized
        if (!dalaranFlow.getProcessingPipeline().isEmpty()) {
            Long firstProcessorId = dalaranFlow.getProcessingPipeline().get(0);
            ProcessorModel firstProcessor = dalaranFlow.getProcessorMap().get(firstProcessorId);
            ProcessorInfo firstProcessorInfo = componentContext.getProcessorInfo(firstProcessor.getType());
            if (firstProcessorInfo.getBodyMode() == BodyMode.Object && dalaranFlow.getInModel() != null) {
                converterContext.unmarshal(route, dalaranFlow.getInModel());
            }
        }
        // TODO flow in model check null
        route.to(FLOW_CAMEL_URI_PREFIX + dalaranFlow.getId());
        if (!dalaranFlow.getProcessingPipeline().isEmpty()) {
            Long lastProcessorId = dalaranFlow.getProcessingPipeline().get(dalaranFlow.getProcessingPipeline().size() - 1);
            ProcessorModel lastProcessor = dalaranFlow.getProcessorMap().get(lastProcessorId);
            ProcessorInfo lastProcessorInfo = componentContext.getProcessorInfo(lastProcessor.getType());
            if (lastProcessorInfo.getBodyMode() == BodyMode.Object && dalaranFlow.getOutModel() != null) {
                converterContext.marshal(route, dalaranFlow.getOutModel());
            }
        }

        // TODO flow out model check null
        tracer.after(route, dalaranFlow.getOutModel().getModelType());
        try {
            camelContext.addRouteDefinition(route);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTestFlows(List<DalaranFlow> flows) {
        flows.forEach(this::addTestFlow);
    }


    // TODO 这里要处理数据的序列化等问题
    @Override
    public Object testFlow(Long flowId, Object body, String recordId) {
        DefaultProducerTemplate template = (DefaultProducerTemplate) camelContext.createProducerTemplate();
        return template.sendBodyAndProperty(TEST_FLOW_CAMEL_URI_PREFIX + flowId, ExchangePattern.InOut, body, TEST_FLOW_RECORD_ID_HEADER, recordId);
    }

    @Override
    public DalaranComponentContext getDalaranComponentContext() {
        return componentContext;
    }

    @Override
    public DalaranConverterContext getDalaranConverterContext() {
        return converterContext;
    }

    private void buildFlowRoute(RouteDefinition route, DalaranFlow dalaranFlow) {
        // TODO route need an u id
        List<Long> processorIdList = dalaranFlow.getProcessingPipeline();
        val triggerInfo = componentContext.getTriggerInfo(dalaranFlow.getTriggerType());

        MessageModel nextMessageModel = null;
        MessageModel currentMessageModel = dalaranFlow.getInModel();

        // TODO 这里一定会先转成 Object, 如果是两端都是序列化类型, 则会有无用的转换
        // TODO 最好的方式是, 在 Flow 上声明出入格式, 由 Trigger 端做处理
        for (int i = 0; i < processorIdList.size(); i++) {
            val processorId = processorIdList.get(i);
            val processor = dalaranFlow.getProcessorMap().get(processorId);
            // TODO check
            val processorComponent = componentContext.getProcessor(processor.getType());
            // TODO check
            val processorInfo = componentContext.getProcessorInfo(processor.getType());
            val spanTracer = DalaranTracer.buildFlowSpanTracer(traceLogger, dalaranFlow.getId(), processor.getId());


            if (processor.getInModel() != null && currentMessageModel.getModelType() != processor.getInModel().getModelType()) {
                currentMessageModel = processor.getInModel();
            }

            // log
            if (BodyMode.Object == processorInfo.getBodyMode()) {
                spanTracer.before(route, BodyModelType.OBJECT);
            } else {
                spanTracer.before(route, currentMessageModel.getModelType());
            }

            processorComponent.configure(route, processor.getConfig());

            if (processor.getOutModel() != null && currentMessageModel.getModelType() != processor.getOutModel().getModelType()) {
                currentMessageModel = processor.getOutModel();
            }
            // log
            if (BodyMode.Object == processorInfo.getBodyMode()) {
                spanTracer.after(route, BodyModelType.OBJECT);
            } else {
                spanTracer.after(route, currentMessageModel.getModelType());
            }

            BodyMode nextBodyMode;
            if (i < processorIdList.size() - 1) {
                val nextProcessorId = processorIdList.get(i + 1);
                val nextProcessor = dalaranFlow.getProcessorMap().get(nextProcessorId);
                val nextProcessorInfo = componentContext.getProcessorInfo(nextProcessor.getType());
                nextBodyMode = nextProcessorInfo.getBodyMode();
                if (nextProcessor.getInModel() != null && nextMessageModel.getModelType() != nextProcessor.getInModel().getModelType()) {
                    nextMessageModel = nextProcessor.getInModel();
                } else {
                    // TODO 下一个节点没有配置出入参, 则以最后一个配置出参类型作为下一个入参类型
                    nextMessageModel = currentMessageModel;
                }

            } else {

                nextBodyMode = triggerInfo.getBodyMode();
                nextMessageModel = dalaranFlow.getOutModel();
            }
            if (processorInfo.getBodyMode() != nextBodyMode) {
                // TODO 类型转化日志, 有必要时可以开启
//                    val convertTracer = DalaranTracer.buildConvertTracer(traceLogger, dalaranFlow.getTriggerId(),
//                            dalaranFlow.getId(), processor.getId());
//                    convertTracer.before(route, currentMessageModel.getModelType());
                if (nextBodyMode == BodyMode.Serialized) {
                    converterContext.marshal(route, nextMessageModel);
                } else {
                    converterContext.unmarshal(route, nextMessageModel);
                }
//                    convertTracer.after(route, nextMessageModel.getModelType());
            }
        }
    }
}
