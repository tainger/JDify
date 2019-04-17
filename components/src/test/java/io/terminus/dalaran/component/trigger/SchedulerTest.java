package io.terminus.dalaran.component.trigger;

import io.terminus.dalaran.component.BasicTriggerTest;
import io.terminus.dalaran.component.trigger.scheduler.DalaranScheduler;
import io.terminus.dalaran.component.trigger.scheduler.DalaranSchedulerConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SchedulerTest extends BasicTriggerTest {

    private List<Long> timeList = new ArrayList<>();

    @Test
    public void test() throws InterruptedException {
        Thread.sleep(10000);
        Assert.assertFalse(timeList.isEmpty());
        for (int i = 1; i < timeList.size(); i++) {
            long timeDifference = timeList.get(i) - timeList.get(i - 1);
            boolean flag = timeDifference < 2100 && timeDifference > 1900;
            // TODO 暂时没有想到行的方式测试.... 目前是起一个每隔两秒执行的调度, 每次执行记录当前时间, 计算每次执行时间间隔是 2±0.1S
            if (!flag) {
                System.out.println(timeDifference + ":" + timeList.get(i) + "-" + timeList.get(i - 1));
            }
            Assert.assertTrue(flag);
        }
        Assert.assertFalse(timeList.isEmpty());
    }


    @Before
    public void before() {
        DalaranScheduler trigger = new DalaranScheduler();
        DalaranSchedulerConfig config = new DalaranSchedulerConfig();
        config.setCron("0/2 * * * * ? *");
        config.setName("test scheduler");

        registerTrigger(trigger, config);
    }

    @Override
    public Object process(Object param) throws Exception {
        timeList.add(System.currentTimeMillis());
        return null;
    }
}
