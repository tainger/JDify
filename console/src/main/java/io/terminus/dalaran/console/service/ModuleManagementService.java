package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.dto.ModuleDTO;
import io.terminus.dalaran.model.dto.ModuleDetailDTO;
import io.terminus.dalaran.model.dto.ModuleFlowDTO;
import io.terminus.dalaran.model.query.ModuleQuery;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
public interface ModuleManagementService {

    String createModule(ModuleDTO moduleModel);

    void deleteModule(String moduleId);

    ModuleDTO updateModule(ModuleDTO moduleModel);

    List<ModuleDTO> list();

    List<ModuleDTO> queryModules(ModuleQuery query);

    ModuleDetailDTO getModuleDetail(String moduleId);

    String getModuleName(@NotNull String moduleId);

    List<ModuleFlowDTO> flowInfoList();
}
