package io.terminus.dalaran;

import io.terminus.dalaran.model.DalaranFlow;

import java.util.List;

public interface DalaranContext {

    void removeFlow(String id) throws Exception;

    void removeFlows(List<String> id);

    void removeAllFlow() throws Exception;

    void addFlow(DalaranFlow flow);

    void addFlows(List<DalaranFlow> flows);

    DalaranComponentContext getDalaranComponentContext();

    DalaranConverterContext getDalaranConverterContext();
}
