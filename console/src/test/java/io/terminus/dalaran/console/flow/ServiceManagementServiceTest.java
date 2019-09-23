package io.terminus.dalaran.console.flow;

import com.alibaba.fastjson.JSON;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.creator.WSDLCreator;
import com.predic8.wsdl.creator.WSDLCreatorContext;
import groovy.xml.MarkupBuilder;
import io.terminus.dalaran.console.entity.ServiceEntity;
import io.terminus.dalaran.console.model.dto.ServiceDTO;
import io.terminus.dalaran.console.model.dto.basic.BasicServiceInfo;
import io.terminus.dalaran.console.repository.ServiceRepository;
import io.terminus.dalaran.console.service.ExportService;
import io.terminus.dalaran.console.service.ServiceManagement;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.StringWriter;
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

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ExportService exportService;

    @Test
    public void exportSoapService() {
        StringWriter stringWriter = new StringWriter();
        WSDLCreator creator = new WSDLCreator();
        creator.setBuilder(new MarkupBuilder(stringWriter));
        Definitions definitions = exportService.exportWSDL();
        definitions.create(creator, new WSDLCreatorContext());
        Assert.assertNotNull(stringWriter);
    }

    @Test
    public void create() {
        ServiceDTO service = buildService();
        Long id = serviceManagement.create(service);
        ServiceEntity entity = serviceRepository.findById(id).get();
        Assert.assertEquals(service.getName(), entity.getName());
    }

    @Test
    public void update() {
        ServiceDTO service = buildService();
        service.setId(1L);
        ServiceDTO newService = serviceManagement.update(service);
        Assert.assertEquals(service.getName(), newService.getName());
    }

    @Test
    public void list() {
        List<ServiceDTO> services = serviceManagement.list();
        Assert.assertNotNull(services);
    }

    @Test
    public void detail() {
        ServiceDTO service = serviceManagement.detail(1L);
        ServiceEntity entity = serviceRepository.findById(1L).get();
        Assert.assertEquals(service.getName(), entity.getName());
    }

    @Test
    public void listOperation() {
        List<String> operations = serviceManagement.listOperation(1L);
        Assert.assertNotNull(operations);
    }

    @Test
    public void listBasicInfoByModuleId() {
        List<BasicServiceInfo> services = serviceManagement.listBasicInfoByModuleId(1L);
        services.forEach(service -> {
            Assert.assertSame(service.getModuleId(), 1L);
        });
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
