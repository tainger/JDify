package io.terminus.dalaran;

import io.terminus.dalaran.model.DalaranFlow;

import java.util.List;

public interface DalaranContext {

    void removeFlow(Long id);

    void removeFlows(List<Long> id);

    void removeAllFlow() throws Exception;

    void addFlow(DalaranFlow flow);

    void addFlows(List<DalaranFlow> flows);

    void addTestFlow(DalaranFlow dalaranFlow);

    void addTestFlows(List<DalaranFlow> flows);

    // TODO 这里要处理数据的序列化等问题
    Object testFlow(Long flowId, Object body, String recordId);

    DalaranComponentContext getDalaranComponentContext();

    DalaranConverterContext getDalaranConverterContext();

}
