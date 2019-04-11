package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.ProcessorModel;
import io.terminus.dalaran.console.model.query.ProcessorQuery;
import io.terminus.dalaran.console.model.query.rst.ModuleComponent;

import java.util.List;

/**
 * Created by jingdi on 2019/3/28
 */
public interface ProcessorManagementService {

    void createProcessor(ProcessorModel processorModel);

    void deleteProcessor(Long processorId);

    void updateProcessor(ProcessorModel processorModel);

    List<ProcessorModel> queryProcessors(ProcessorQuery query);

    List<ProcessorModel> list();

    List<ModuleComponent> getComponents(Long moduleId);
}
