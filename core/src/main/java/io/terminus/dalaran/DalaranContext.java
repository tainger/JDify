package io.terminus.dalaran;

import io.terminus.dalaran.model.flow.TriggerFlow;

import java.util.List;

public interface DalaranContext {

    void removeFlow(Long id);

    void removeFlows(List<Long> id);

    void removeAllFlow() throws Exception;

    void addTriggerFlow(TriggerFlow flow);

    void addTriggerFlows(List<TriggerFlow> flows);

    // TODO 这里要处理数据的序列化等问题
    Object testFlow(Long flowId, Object body, String recordId);

    DalaranComponentContext getDalaranComponentContext();

    DalaranConverterContext getDalaranConverterContext();

}
