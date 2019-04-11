package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.ModuleModel;
import io.terminus.dalaran.console.model.query.ModuleQuery;
import io.terminus.dalaran.console.model.query.rst.ModuleComponent;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
public interface ModuleManagementService {

    void createModule(ModuleModel moduleModel);

    void deleteModule(Long moduleId);

    void updateModule(ModuleModel moduleModel);

    List<ModuleModel> list();

    List<ModuleModel> queryModules(ModuleQuery query);

    List<ModuleComponent> listModuleComponents(Long moduleId);
}
