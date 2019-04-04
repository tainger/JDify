package io.terminus.dalaran.support.flow;

import io.terminus.dalaran.BodyMode;
import io.terminus.dalaran.DalaranComponentContext;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.DalaranConverterContext;
import io.terminus.dalaran.model.DalaranFlow;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.TriggerModel;
import lombok.val;
import org.apache.camel.CamelContext;
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
        route.setId(dalaranFlow.getId());
        val processorList = dalaranFlow.getProcessors();
        route.from("direct:flow-" + dalaranFlow.getId());

        // TODO 这里一定会先转成 Object, 如果是两端都是序列化类型, 则会有无用的转换
        // TODO 最好的方式是, 在 Flow 上声明出入格式, 由 Trigger 端做处理
        BodyMode currentBodyMode = BodyMode.Object;
        MessageModel currentStructure = null;
        for (int i = 0; i < processorList.size(); i++) {
            val processor = processorList.get(i);
            val processorComponent = componentContext.getProcessor(processor.getType());
            val processorInfo = componentContext.getProcessorInfo(processor.getType());
            // TODO check
            val nextBodyMode = processorInfo.getBodyMode();
            if (i != processorList.size() && currentBodyMode != nextBodyMode) {
                if (nextBodyMode == BodyMode.Serialized) {
                    if (processor.getInModel() != null) {
                        converterContext.marshal(route, processor.getInModel());
                    }
                } else {
                    if (currentStructure != null) {
                        converterContext.unmarshal(route, currentStructure);
                    }
                }
                if (processor.getOutModel() != null) {
                    currentStructure = processor.getOutModel();
                }
            }

            route.to("log:processor[" + processor.getId() + "]?showAll=true&multiline=true");
            processorComponent.configure(route, processor.getConfig());
            currentBodyMode = processorInfo.getBodyMode();
        }
        // TODO 流程最后不可得知触发器的出模型, 所以无法判断做格式转换, 最保险的方式是固定转为 Object, 在 trigger 端在根据要求做一次序列化, 但是会有性能损耗
        // TODO 另外这里也不好判断是否是最后的节点, 因为存在分支, 暂时将最后节点作为流输出节点
        // TODO 也可以考虑加一个动态节点, 根据上下文判断如何做处理, 这样就没办法用 camel DSL 了
        if (currentBodyMode == BodyMode.Serialized) {
            if (currentStructure != null) {
                converterContext.unmarshal(route, currentStructure);
            }
        }
        // TODO on exception...
        try {
            camelContext.addRouteDefinition(route);
        } catch (Exception e) {
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

        triggerComponent.buildFromRoute(route, trigger.getConfig());
        route.to("log:trigger[" + trigger.getId() + "]?showAll=true&multiline=true");
        val triggerInfo = componentContext.getTriggerInfo(trigger.getType());
        if (triggerInfo.getBodyMode() == BodyMode.Serialized && trigger.getInModel() != null) {
            converterContext.unmarshal(route, trigger.getInModel());
        }
        route.to("direct:flow-" + trigger.getFlowId());
        if (triggerInfo.getBodyMode() == BodyMode.Serialized && trigger.getOutModel() != null) {
            converterContext.marshal(route, trigger.getOutModel());
        }
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
