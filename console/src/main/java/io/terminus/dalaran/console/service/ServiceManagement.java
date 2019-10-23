package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.component.ServiceOperation;
import io.terminus.dalaran.model.dto.ServiceDTO;
import io.terminus.dalaran.model.dto.basic.BasicServiceInfo;

import java.util.List;

public interface ServiceManagement {

    Long create(ServiceDTO serviceDTO);

    ServiceDTO update(ServiceDTO serviceDTO);

    void delete(Long serviceId);

    ServiceDTO detail(Long serviceId);

    List<ServiceDTO> list();

    List<ServiceOperation> listOperation(Long serviceId);

    List<BasicServiceInfo> listBasicInfoByModuleId(Long moduleId);
}
