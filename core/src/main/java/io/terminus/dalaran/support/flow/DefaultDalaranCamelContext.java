package io.terminus.dalaran.support.flow;

import io.terminus.dalaran.*;
import io.terminus.dalaran.model.DalaranFlow;
import io.terminus.dalaran.model.TriggerModel;
import io.terminus.dalaran.support.trace.DalaranTracer;
import io.terminus.dalaran.support.trace.TracingType;
import lombok.val;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.Builder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

import static io.terminus.dalaran.DalaranConstants.*;

public class DefaultDalaranCamelContext implements DalaranContext {

    private final CamelContext camelContext;

    private final DalaranConverterContext converterContext;

    private final DalaranComponentContext componentContext;

    private final DalaranTraceLogger traceLogger;

    public DefaultDalaranCamelContext(DalaranConverterContext converterContext, DalaranComponentContext componentContext, DalaranTraceLogger traceLogger) {
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

        val route = new RouteDefinition();
        // TODO route need an u id
        route.setId(FLOW_PREFIX + dalaranFlow.getId());
        val processorList = dalaranFlow.getProcessors();
        route.from(FLOW_CAMEL_URI_PREFIX + dalaranFlow.getId());
        // TODO use const
        route.setProperty(CURRENT_FLOW_ID, Builder.constant(dalaranFlow.getId()));

        // TODO 这里一定会先转成 Object, 如果是两端都是序列化类型, 则会有无用的转换
        // TODO 最好的方式是, 在 Flow 上声明出入格式, 由 Trigger 端做处理
        for (int i = 0; i < processorList.size(); i++) {
            val processor = processorList.get(i);

            val processorComponent = componentContext.getProcessor(processor.getType());
            val processorInfo = componentContext.getProcessorInfo(processor.getType());
            // TODO check
            val tracer = new DalaranTracer(traceLogger, TracingType.Flow);
            BodyModelType inputType;
            if (processor.getInModel() != null) {
                inputType = processor.getInModel().getModelType();
            } else {
                inputType = BodyModelType.OBJECT;
            }
            route.setProperty(CURRENT_PROCESSOR_ID, Builder.constant(processor.getId()));
            // log
            route.process(tracer.buildBeforeProcessor(inputType));

            processorComponent.configure(route, processor.getConfig());
            if (i < processorList.size() - 1) {
                val nextProcessor = processorList.get(i + 1);
                val nextProcessorInfo = componentContext.getProcessorInfo(nextProcessor.getType());
                if (processorInfo.getBodyMode() != nextProcessorInfo.getBodyMode()) {
                    if (nextProcessorInfo.getBodyMode() == BodyMode.Serialized) {
                        if (nextProcessor.getInModel() != null) {
                            converterContext.marshal(route, nextProcessor.getInModel());
                        }
                    } else if (processor.getOutModel() != null) {
                        converterContext.unmarshal(route, processor.getOutModel());
                    }
                }
            } else if (processorInfo.getBodyMode() == BodyMode.Serialized && processor.getOutModel() != null) {
                converterContext.unmarshal(route, processor.getOutModel());
            }

            BodyModelType outputType;
            if (processor.getInModel() != null) {
                outputType = processor.getOutModel().getModelType();
            } else {
                outputType = BodyModelType.OBJECT;
            }
            // log
            route.process(tracer.buildAfterProcessor(outputType));
        }
        // TODO 流程最后不可得知触发器的出模型, 所以无法判断做格式转换, 最保险的方式是固定转为 Object, 在 trigger 端在根据要求做一次序列化, 但是会有性能损耗
        // TODO 另外这里也不好判断是否是最后的节点, 因为存在分支, 暂时将最后节点作为流输出节点
        // TODO 也可以考虑加一个动态节点, 根据上下文判断如何做处理, 这样就没办法用 camel DSL 了

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
    public void addTrigger(TriggerModel trigger) {
        try {
            camelContext.removeRoute(TRIGGER_PREFIX + trigger.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        val triggerComponent = componentContext.getTrigger(trigger.getType());
        val triggerInfo = componentContext.getTriggerInfo(trigger.getType());
        val tracer = new DalaranTracer(traceLogger, TracingType.Trigger);

        val route = new RouteDefinition();
        route.setId(TRIGGER_PREFIX + trigger.getId());
        route.setProperty(CURRENT_TRIGGER_ID, Builder.constant(trigger.getId()));


        triggerComponent.buildFromRoute(route, trigger.getConfig());
        // TODO 这里要判断的是流和触发器的 body 类型
        if (triggerInfo.getBodyMode() == BodyMode.Serialized && trigger.getInModel() != null) {
            converterContext.unmarshal(route, trigger.getInModel());
        }
        route.process(tracer.buildBeforeProcessor(trigger.getInModel().getModelType()));
        route.to(FLOW_CAMEL_URI_PREFIX + trigger.getFlow().getId());
        if (triggerInfo.getBodyMode() == BodyMode.Serialized && trigger.getOutModel() != null) {
            converterContext.marshal(route, trigger.getOutModel());
        }

        route.process(tracer.buildAfterProcessor(trigger.getOutModel().getModelType()));
        try {
            camelContext.addRouteDefinition(route);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTriggers(List<TriggerModel> triggers) {
        triggers.forEach(this::addTrigger);
    }


    // TODO 这里要处理数据的序列化等问题
    @Override
    public Object testFlow(Long id, Object body) {
        ProducerTemplate template = camelContext.createProducerTemplate();
//        template.sendBody(, body);
        template.setDefaultEndpointUri(FLOW_CAMEL_URI_PREFIX + id);

        return template.requestBody(body);
    }

    @Override
    public DalaranComponentContext getDalaranComponentContext() {
        return componentContext;
    }

    @Override
    public DalaranConverterContext getDalaranConverterContext() {
        return converterContext;
    }
}
