package io.terminus.dalaran.console.flow;

import io.terminus.dalaran.console.service.TracingLogService;
import io.terminus.dalaran.core.resource.repository.TracingLogRepository;
import io.terminus.dalaran.model.dto.log.MainLogDTO;
import io.terminus.dalaran.model.query.TracingLogQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by jingdi on 2019/6/30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
public class TracingLogManagementServiceTest {

    @Autowired
    private TracingLogService tracingLogService;

    @Autowired
    private TracingLogRepository tracingLogRepository;

    @Test
    public void triggerLogs() {
        TracingLogQuery query = new TracingLogQuery();
        query.setModuleId("1L");
        query.setFlowId("4L");
        List<MainLogDTO> logs = tracingLogService.triggerLogs(query);
        logs.forEach(log -> {
            Assert.assertSame(log.getFlowId(), 4L);
        });
    }

    @Test
    public void getRecordDetail() {
        String recordId = "p64JkTTBZgc0hu4S";
        MainLogDTO log = tracingLogService.getRecordDetail(recordId);
        Assert.assertEquals(log.getRecordId(), recordId);
    }
}
