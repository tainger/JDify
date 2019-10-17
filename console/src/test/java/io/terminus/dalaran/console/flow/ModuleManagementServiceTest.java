package io.terminus.dalaran.console.flow;

import io.terminus.dalaran.console.service.ModuleManagementService;
import io.terminus.dalaran.core.resource.entity.common.ModuleEntity;
import io.terminus.dalaran.core.resource.repository.ModuleRepository;
import io.terminus.dalaran.model.dto.ModuleDTO;
import io.terminus.dalaran.model.dto.ModuleDetailDTO;
import io.terminus.dalaran.model.query.ModuleQuery;
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

    @Autowired
    private ModuleRepository moduleRepository;

    @Test
    public void create() {
        ModuleDTO module = new ModuleDTO();
        module.setName("test-module");
        Long id = moduleManagementService.createModule(module);
        ModuleEntity entity = moduleRepository.findById(id).get();
        Assert.assertEquals(entity.getName(), module.getName());
    }

    @Test
    public void update() {
        ModuleDTO module = new ModuleDTO();
        module.setName("update-module");
        module.setDescription("update");
        ModuleDTO newModule = moduleManagementService.updateModule(module);
        Assert.assertEquals(newModule.getName(), module.getName());
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
        Assert.assertSame(modules.size(), 1);
    }

    @Test
    public void getModuleDetail() {
        ModuleDetailDTO moduleDetail = moduleManagementService.getModuleDetail(1L);
        ModuleEntity entity = moduleRepository.findById(1L).get();
        Assert.assertEquals(moduleDetail.getName(), entity.getName());
    }

    @Test
    public void getModuleName() {
        String name = moduleManagementService.getModuleName(1L);
        ModuleEntity entity = moduleRepository.findById(1L).get();
        Assert.assertEquals(name, entity.getName());
    }
}
