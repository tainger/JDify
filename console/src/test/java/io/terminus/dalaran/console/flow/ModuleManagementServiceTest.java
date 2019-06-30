package io.terminus.dalaran.console.flow;

import io.terminus.dalaran.console.model.dto.ModuleDTO;
import io.terminus.dalaran.console.model.dto.ModuleDetailDTO;
import io.terminus.dalaran.console.model.query.ModuleQuery;
import io.terminus.dalaran.console.service.ModuleManagementService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jingdi on 2019/6/30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
public class ModuleManagementServiceTest {

    @Autowired
    private ModuleManagementService moduleManagementService;

    @Test
    public void create() {
        ModuleDTO module = new ModuleDTO();
        module.setName("test-module");
        Long id = moduleManagementService.createModule(module);
        Assert.assertNotNull(id);
    }

    @Test
    public void update() {
        ModuleDTO module = new ModuleDTO();
        module.setName("update-module");
        module.setDescription("update");
        ModuleDTO newModule = moduleManagementService.updateModule(module);
        Assert.assertNotNull(newModule);
    }

    @Test
    public void list() {
        List<ModuleDTO> modules = moduleManagementService.list();
        Assert.assertNotNull(modules);
    }

    @Test
    public void query() {
        ModuleQuery query = new ModuleQuery();
        List<Long> moduleIds = new ArrayList<>();
        moduleIds.add(1L);
        query.setModuleIds(moduleIds);
        List<ModuleDTO> modules = moduleManagementService.queryModules(query);
        Assert.assertNotNull(modules);
    }

    @Test
    public void getModuleDetail() {
        ModuleDetailDTO moduleDetail = moduleManagementService.getModuleDetail(1L);
        Assert.assertNotNull(moduleDetail);
    }

    @Test
    public void getModuleName() {
        String name = moduleManagementService.getModuleName(1L);
        Assert.assertNotNull(name);
    }
}
