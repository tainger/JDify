package io.terminus.dalaran.console.flow;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.model.dto.BasicServiceInfo;
import io.terminus.dalaran.console.model.dto.ServiceDTO;
import io.terminus.dalaran.console.service.ServiceManagement;
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
public class ServiceManagementServiceTest {

    @Autowired
    private ServiceManagement serviceManagement;

    @Test
    public void create() {
        Long id = serviceManagement.create(buildService());
        Assert.assertNotNull(id);
    }

    @Test
    public void update() {
        ServiceDTO service = buildService();
        service.setId(1L);
        ServiceDTO newService = serviceManagement.update(service);
        Assert.assertNotNull(newService);
    }

    @Test
    public void list() {
        List<ServiceDTO> services = serviceManagement.list();
        Assert.assertNotNull(services);
    }

    @Test
    public void detail() {
        ServiceDTO service = serviceManagement.detail(1L);
        Assert.assertNotNull(service);
    }

    @Test
    public void listOperation() {
        List<String> operations = serviceManagement.listOperation(1L);
        Assert.assertNotNull(operations);
    }

    @Test
    public void listBasicInfoByModuleId() {
        List<BasicServiceInfo> services = serviceManagement.listBasicInfoByModuleId(1L);
        Assert.assertNotNull(services);
    }

    private ServiceDTO buildService() {
        ServiceDTO service = new ServiceDTO();
        service.setModuleId(1L);
        service.setName("test-service");
        service.setType("swagger-connector");
        service.setDescription("create");

        /**
         * {"swaggerUrl":"https://dev-dalaran-api.app.terminus.io/v2/api-docs"}
         */
        String s1 = "{\"swaggerUrl\":\"https://dev-dalaran-api.app.terminus.io/v2/api-docs\"}";
        Map<String, Object> importConfig = JSON.parseObject(s1, Map.class);
        service.setImportConfig(importConfig);

        /**
         * {"basePath":"/","configs":[{"inModel":{"modelType":"JSON","modelSchema":{"fields":{}}},"method":"OPTIONS","path":"/error","url":"dev-dalaran-api.app.terminus.io/"}],"url":"dev-dalaran-api.app.terminus.io"}
         */
        String s2 = "{\"basePath\":\"/\",\"configs\":[{\"inModel\":{\"modelType\":\"JSON\",\"modelSchema\":{\"fields\":{}}},\"method\":\"OPTIONS\",\"path\":\"/error\",\"url\":\"dev-dalaran-api.app.terminus.io/\"}],\"url\":\"dev-dalaran-api.app.terminus.io\"}";
        Map<String, Object> serviceConfig = JSON.parseObject(s2, Map.class);
        service.setServiceConfig(serviceConfig);

        return service;
    }

}
