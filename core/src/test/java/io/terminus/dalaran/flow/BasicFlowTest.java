package io.terminus.dalaran.flow;

import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.model.DalaranFlow;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ProcessorModel;
import io.terminus.dalaran.model.TriggerModel;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class BasicFlowTest {

    @Autowired
    private DalaranContext dalaranContext;

    @Test
    public void test() {
        // TODO 需要被抽象, 理论上测试 flow, trigger 和 test flow 都需要这部分数据
        List<ProcessorModel> processorModelList = new ArrayList<>();
        ProcessorModel processorModel = new ProcessorModel();

        MessageModel inModel = new MessageModel();
        MessageModel outModel = new MessageModel();


        DalaranFlow flow = new DalaranFlow();
        flow.setProcessors(processorModelList);
        flow.setInModel(inModel);
        flow.setOutModel(outModel);
        flow.setId(1L);

        TriggerModel trigger = new TriggerModel();

        dalaranContext.addFlow(flow);
        dalaranContext.addTestFlow(flow);
        dalaranContext.addTrigger(trigger);


    }
}
