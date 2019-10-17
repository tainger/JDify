package io.terminus.dalaran.core.component;

import io.terminus.dalaran.model.flow.TriggerFlow;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface DalaranTriggerWordDocExport {

    File exportWord(Map<String, List<TriggerFlow>> moduleTriggerFlows);
}
