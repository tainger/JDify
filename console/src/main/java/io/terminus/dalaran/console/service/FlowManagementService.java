package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.entity.FlowEntity;

public interface FlowManagementService {

    void saveFlow(FlowEntity flowEntity);

    void publish();

}
