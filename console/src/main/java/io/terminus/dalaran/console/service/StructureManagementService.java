package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.StructureModel;
import io.terminus.dalaran.console.model.query.StructureQuery;
import io.terminus.dalaran.console.model.query.rst.ModuleComponent;

import java.util.List;

/**
 * Created by jingdi on 2019/3/28
 */
public interface StructureManagementService {

    void createStructure(StructureModel structureModel);

    void deleteStructure(Long structureId);

    void updateStructure(StructureModel structureModel);

    List<StructureModel> queryStructures(StructureQuery query);

    List<StructureModel> list();

    List<ModuleComponent> getComponents(Long moduleId);
}