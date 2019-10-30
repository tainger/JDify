package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.OnException;
import io.terminus.dalaran.console.service.ModuleManagementService;
import io.terminus.dalaran.model.dto.ModuleDTO;
import io.terminus.dalaran.model.dto.ModuleDetailDTO;
import io.terminus.dalaran.model.query.ModuleQuery;
import io.terminus.dalaran.rest.read.ModuleReadAPI;
import io.terminus.dalaran.rest.write.ModuleWriteAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@RestController
@RequestMapping("/api/module")
public class ModuleManagementRest implements ModuleReadAPI, ModuleWriteAPI {

    @Autowired
    private ModuleManagementService moduleManagementService;

    @Override
    @OnException(code = ResponseMessage.MODULE_QUERY_ERROR)
    public List<ModuleDTO> query(ModuleQuery query) {
        return moduleManagementService.queryModules(query);
    }

    @Override
    @OnException(code = ResponseMessage.MODULE_CREATE_ERROR)
    public Long create(@RequestBody ModuleDTO model) {
        return moduleManagementService.createModule(model);
    }

    @Override
    @OnException(code = ResponseMessage.MODULE_UPDATE_ERROR)
    public ModuleDTO update(@RequestBody ModuleDTO model) {
        return moduleManagementService.updateModule(model);
    }

    @Override
    @OnException(code = ResponseMessage.MODULE_DELETE_ERROR)
    public void deleteById(@RequestParam Long id) {
        moduleManagementService.deleteModule(id);
    }

    @Override
    @OnException(code = ResponseMessage.MODULE_QUERY_ERROR)
    public List<ModuleDTO> list() {
        return moduleManagementService.list();
    }

    @Override
    @OnException(code = ResponseMessage.MODULE_QUERY_ERROR)
    public ModuleDetailDTO moduleDetail(@PathVariable Long id) {
        return moduleManagementService.getModuleDetail(id);
    }
}
