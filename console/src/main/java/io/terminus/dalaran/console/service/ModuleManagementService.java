package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.dto.ModuleDTO;
import io.terminus.dalaran.console.model.query.ModuleQuery;
import io.terminus.dalaran.console.model.query.rst.ModuleComponent;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
public interface ModuleManagementService {

    Long createModule(ModuleDTO moduleModel);

    void deleteModule(Long moduleId);

    ModuleDTO updateModule(ModuleDTO moduleModel);

    List<ModuleDTO> list();

    List<ModuleDTO> queryModules(ModuleQuery query);

    List<ModuleComponent> listModuleComponents(Long moduleId);
}
