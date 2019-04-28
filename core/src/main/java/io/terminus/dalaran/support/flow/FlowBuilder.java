package io.terminus.dalaran.support.flow;

import io.terminus.dalaran.*;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ProcessorModel;
import io.terminus.dalaran.model.config.ProcessorInfo;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;
import io.terminus.dalaran.support.trace.DalaranTracer;
import io.terminus.dalaran.support.trace.TracingErrorHandlerFactory;
import lombok.val;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

import static io.terminus.dalaran.DalaranConstants.FLOW_PREFIX;

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

    public RouteDefinition buildTriggerFlow(TriggerFlow flow) {
        val triggerComponent = componentContext.getTrigger(flow.getTriggerType());
        val flowTracer = DalaranTracer.buildFlowTracer(traceLogger, flow.getId());
        val route = new RouteDefinition();
        route.setId(FLOW_PREFIX + flow.getId());
        route.errorHandler(errorHandlerFactory);
        triggerComponent.buildFromRoute(route, flow.getTriggerConfig());
        flowTracer.before(route, flow.getInModel().getModelType());

        buildFlowRoute(route, flow);
        // TODO 流程最后不可得知触发器的出模型, 所以无法判断做格式转换, 最保险的方式是固定转为 Object, 在 trigger 端在根据要求做一次序列化, 但是会有性能损耗
        // TODO 另外这里也不好判断是否是最后的节点, 因为存在分支, 暂时将最后节点作为流输出节点
        // TODO 也可以考虑加一个动态节点, 根据上下文判断如何做处理, 这样就没办法用 camel DSL 了
        flowTracer.after(route, flow.getOutModel().getModelType());
        return route;
    }

    public RouteDefinition buildSubFLow(SubFlow flow) {
        return null;
    }

    private void buildFlowRoute(RouteDefinition route, BasicFlow flow) {
        // TODO route need an u id
        List<ProcessorModel> processorList = flow.getPipeline();
        MessageModel lastedMessageModel = flow.getInModel();
        BodyType currentBodyType;
        if (flow.getInModel() == null) {
            currentBodyType = BodyType.OBJECT;
        } else {
            currentBodyType = flow.getInModel().getModelType();
        }

        // TODO 这里一定会先转成 Object, 如果是两端都是序列化类型, 则会有无用的转换
        // TODO 最好的方式是, 在 Flow 上声明出入格式, 由 Trigger 端做处理
        for (int i = 0; i < processorList.size(); i++) {
            ProcessorModel processor = processorList.get(i);

            DalaranTracer spanTracer = DalaranTracer.buildFlowSpanTracer(traceLogger, flow.getId(), processor.getId());
            DalaranProcessor processorComponent = componentContext.getProcessor(processor.getType());
            ProcessorInfo processorInfo = componentContext.getProcessorInfo(processor.getType());

            MessageModel inModel = getProcessorInModel(processor);

            spanTracer.before(route, currentBodyType);

            /*
             * 如果下一个节点不允许当前的数据类型, 则进行数据格式转换
             * 如果下个节点有明确入参, 则按照入参声明转换
             * 如果没有, 尝试查看最后一次有效的数据模型是否允许, 允许则使用最后一次模型进行转换
             * 如果都没有, 选择下一个节点第一个可接受类型进行转换
             */
            if (!processorInfo.allowedBodyType(currentBodyType)) {
                // 如果
                if (inModel != null) {
                    converterContext.convert(route, currentBodyType, inModel);
                    lastedMessageModel = inModel;
                    currentBodyType = inModel.getModelType();
                } else if (lastedMessageModel != null && processorInfo.allowedBodyType(lastedMessageModel.getModelType())) {
                    converterContext.convert(route, currentBodyType, lastedMessageModel);
                    currentBodyType = lastedMessageModel.getModelType();
                } else {
                    BodyType nextBodyType = processorInfo.firstAllowedBodyType();
                    converterContext.convert(route, currentBodyType, nextBodyType);
                    currentBodyType = nextBodyType;
                }
            }

            processorComponent.configure(route, processor.getConfig());


            MessageModel outModel = getProcessorOutModel(processor);

            if (outModel != null) {
                currentBodyType = outModel.getModelType();
            }
            spanTracer.after(route, currentBodyType);

            // last processor
            if (i == processorList.size() - 1 && flow.getOutModel() != null && flow.getOutModel().getModelType() != currentBodyType) {
                converterContext.convert(route, currentBodyType, flow.getOutModel());
            }
        }
    }

    private MessageModel getProcessorInModel(ProcessorModel processor) {
        if (processor.getConfig() instanceof ModelableConfig) {
            return ((ModelableConfig) processor.getConfig()).getInModel();
        }
        return null;
    }

    private MessageModel getProcessorOutModel(ProcessorModel processor) {
        if (processor.getConfig() instanceof ModelableConfig) {
            return ((ModelableConfig) processor.getConfig()).getOutModel();
        }
        return null;
    }
}
