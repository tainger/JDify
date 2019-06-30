package io.terminus.dalaran.console.flow;

import io.terminus.dalaran.console.model.dto.log.MainLogDTO;
import io.terminus.dalaran.console.model.query.TracingLogQuery;
import io.terminus.dalaran.console.service.TracingLogService;
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

    @Test
    public void triggerLogs() {
        TracingLogQuery query = new TracingLogQuery();
        query.setModuleId(1L);
        query.setFlowId(1L);
        List<MainLogDTO> logs = tracingLogService.triggerLogs(query);
        Assert.assertNotNull(logs);
    }

    @Test
    public void getRecordDetail() {
        MainLogDTO log = tracingLogService.getRecordDetail("ID-jingdideMacBook-Pro-local-1556007795710-0-1");
        Assert.assertNotNull(log);
    }
}
