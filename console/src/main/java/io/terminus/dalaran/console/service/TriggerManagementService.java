package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.TriggerModel;
import io.terminus.dalaran.console.model.query.TriggerQuery;
import io.terminus.dalaran.console.model.query.rst.ModuleComponent;
import io.terminus.dalaran.model.config.TriggerInfo;

import java.util.Collection;
import java.util.List;

/**
 * Created by jingdi on 2019/3/28
 */
public interface TriggerManagementService {

    void createTrigger(TriggerModel triggerModel);

    void deleteTrigger(Long triggerId);

    void updateTrigger(TriggerModel triggerModel);

    List<TriggerModel> queryTriggers(TriggerQuery query);

    List<TriggerModel> list();

    List<ModuleComponent> getComponents(Long moduleId);

    Collection<TriggerInfo> listTriggers();

    TriggerInfo getTriggerInfo(String triggerType);
}
