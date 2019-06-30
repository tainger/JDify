package io.terminus.dalaran.console.flow;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.model.dto.BasicConnectorInfo;
import io.terminus.dalaran.console.model.dto.ConnectorDTO;
import io.terminus.dalaran.console.service.ConnectorService;
import io.terminus.dalaran.core.component.ComponentType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/6/30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
public class ConnectorManagementServiceTest {

    @Autowired
    private ConnectorService connectorService;

    @Test
    public void create() {
        Long id = connectorService.create(buildConnector());
        Assert.assertNotNull(id);
    }

    @Test
    public void update() {
        ConnectorDTO connector = buildConnector();
        connector.setId(4L);
        ConnectorDTO newConnector = connectorService.update(connector);
        Assert.assertNotNull(newConnector);
    }

    @Test
    public void detail() {
        ConnectorDTO connector = connectorService.detail(4L);
        Assert.assertNotNull(connector);
    }

    @Test
    public void listBasicInfoByModuleId() {
        List<BasicConnectorInfo> connectors = connectorService.listBasicInfoByModuleId(1L);
        Assert.assertNotNull(connectors);
    }

    @Test
    public void listBasicInfoByComponent() {
        List<BasicConnectorInfo> connectors = connectorService.listBasicInfoByComponent(ComponentType.Processor, "http-client");
        Assert.assertNotNull(connectors);
    }

    @Test
    @Rollback
    public void delete() {
        connectorService.delete(4L);
    }

    private ConnectorDTO buildConnector() {
        ConnectorDTO connector = new ConnectorDTO();
        connector.setModuleId(1L);
        connector.setName("test-connector");
        connector.setComponentType(ComponentType.Processor);
        connector.setComponentName("http-client");

        /**
         * {"host":"localhost","protocol":"HTTP","port":"8080","timeout":"3000"}
         */
        String str = "{\"host\":\"localhost\",\"protocol\":\"HTTP\",\"port\":\"8080\",\"timeout\":\"3000\"}";
        Map<String, Object> config = JSON.parseObject(str, Map.class);
        connector.setConfig(config);

        return connector;
    }
}
