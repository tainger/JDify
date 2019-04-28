package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.dto.ModelDTO;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.model.query.ModelQuery;
import io.terminus.dalaran.console.model.query.rst.ModuleComponent;
import io.terminus.dalaran.entity.ModelEntity;

import java.util.List;

/**
 * Created by jingdi on 2019/3/28
 */
public interface ModelManagementService {

    Long createModel(ModelDTO modelModel);

    void deleteModel(Long modelId);

    ModelDTO updateModel(ModelDTO modelModel);

    List<ModelDTO> queryModels(ModelQuery query);

    List<ModelDTO> list();

    List<ModuleComponent> getComponents(Long moduleId);

    ModelEntity getById(Long modelId);
}