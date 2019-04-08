package io.terminus.dalaran.support.flow;

import io.terminus.dalaran.*;
import io.terminus.dalaran.model.*;
import io.terminus.dalaran.support.trace.DalaranTracer;
import lombok.val;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.Builder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

public class DefaultDalaranCamelContext implements DalaranContext {

    private final CamelContext camelContext;

    private DalaranConverterContext converterContext;

    private DalaranComponentContext componentContext;

    public DefaultDalaranCamelContext(DalaranConverterContext converterContext, DalaranComponentContext componentContext) {
        this.converterContext = converterContext;
        this.componentContext = componentContext;
        this.camelContext = new DefaultCamelContext();
        try {
//            Tracer tracer = Tracer.createTracer(camelContext);
//            tracer.setEnabled(true);
//            tracer.setTraceOutExchanges(true);
//            tracer.setTraceHandler(new DalaranTracer2());
//            camelContext.setDefaultTracer(tracer);
//            camelContext.setTracing(true);
//            camelContext.addInterceptStrategy(new DalaranTracer());
            camelContext.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeFlow(String flowId) throws Exception {
        camelContext.removeRoute(flowId);
    }

    @Override
    public void removeFlows(List<String> flowIds) {
        flowIds.forEach(flowId -> {
            try {
                camelContext.removeRoute(flowId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void removeAllFlow() throws Exception {
        camelContext.removeRouteDefinitions(camelContext.getRouteDefinitions());
    }

    // TODO Flow 可以抽象一个 Builder, 但暂时没有必要
    @Override
    public void addFlow(DalaranFlow dalaranFlow) {
        val route = new RouteDefinition();
        // TODO route need an u id
        route.setId("flow-" + dalaranFlow.getId());
        val processorList = dalaranFlow.getProcessors();
        route.from("direct:flow-" + dalaranFlow.getId());
        // TODO use const
        route.setProperty("flow_id", Builder.constant(dalaranFlow.getId()));

        // TODO 这里一定会先转成 Object, 如果是两端都是序列化类型, 则会有无用的转换
        // TODO 最好的方式是, 在 Flow 上声明出入格式, 由 Trigger 端做处理
        for (int i = 0; i < processorList.size(); i++) {
            val processor = processorList.get(i);

            val processorComponent = componentContext.getProcessor(processor.getType());
            val processorInfo = componentContext.getProcessorInfo(processor.getType());
            // TODO check
            val tracer = new DalaranTracer(processor.getId());
            BodyModelType inputType;
            if (processor.getInModel() != null) {
                inputType = processor.getInModel().getModelType();
            } else {
                inputType = BodyModelType.OBJECT;
            }
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
        val route = new RouteDefinition();
        val triggerComponent = componentContext.getTrigger(trigger.getType());
        route.setProperty("trigger_id", Builder.constant(trigger.getId()));
        triggerComponent.buildFromRoute(route, trigger.getConfig());
        val triggerInfo = componentContext.getTriggerInfo(trigger.getType());
        val tracer = new DalaranTracer();

        route.process(tracer.buildBeforeProcessor(trigger.getInModel().getModelType()));
        if (triggerInfo.getBodyMode() == BodyMode.Serialized && trigger.getInModel() != null) {
            converterContext.unmarshal(route, trigger.getInModel());
        }
        route.to("direct:flow-" + trigger.getFlow().getId());
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
