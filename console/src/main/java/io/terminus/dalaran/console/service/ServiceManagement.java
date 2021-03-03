package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.component.ServiceOperation;
import io.terminus.dalaran.model.dto.ServiceDTO;
import io.terminus.dalaran.model.dto.basic.BasicServiceInfo;

import java.util.List;

public interface ServiceManagement {

    String create(ServiceDTO serviceDTO);

    ServiceDTO update(ServiceDTO serviceDTO);

    void delete(String serviceId);

    ServiceDTO detail(String serviceId);

    List<ServiceDTO> list();

    List<ServiceOperation> listOperation(String serviceId);

    List<BasicServiceInfo> listBasicInfoByModuleId(String moduleId);
}
