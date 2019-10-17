package io.terminus.dalaran.core.component;

import io.terminus.dalaran.model.flow.TriggerFlow;

import java.util.List;
import java.util.Map;

public interface DalaranTriggerApiDocExport<T> {

    T exportApiDoc(Map<String, List<TriggerFlow>> moduleTriggerFlows);
}
