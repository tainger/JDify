package io.terminus.dalaran.console.flow;

import io.terminus.dalaran.console.model.ReleaseRequestDTO;
import io.terminus.dalaran.console.model.dto.ReleaseRecordDTO;
import io.terminus.dalaran.console.service.ReleaseService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by jingdi on 2019/7/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
public class ReleaseManagementServiceTest {

    @Autowired
    private ReleaseService releaseService;

    private static final String version = "1.0.0";

    @Test
    public void release() {
        ReleaseRequestDTO releaseRequest = new ReleaseRequestDTO();
        releaseRequest.setVersion(version);
        releaseRequest.setReleaseLog("release version 1.0.0");
        ReleaseRecordDTO releaseRecord = releaseService.release(releaseRequest);
        Assert.assertEquals(releaseRecord.getVersion(), version);
    }

    @Test
    public void rollback() {
        ReleaseRecordDTO releaseRecord = releaseService.rollback(version);
        Assert.assertEquals(releaseRecord.getVersion(), version);
    }
}
