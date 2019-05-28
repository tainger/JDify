package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.dto.BasicModelInfo;
import io.terminus.dalaran.console.model.dto.BasicServiceInfo;
import io.terminus.dalaran.console.model.dto.ServiceDTO;

import java.util.List;

public interface ServiceManagement {

    Long create(ServiceDTO serviceDTO);

    ServiceDTO update(ServiceDTO serviceDTO);

    void delete(Long serviceId);

    ServiceDTO detail(Long serviceId);

    List<ServiceDTO> list();

    List<String> listOperation(Long serviceId);

    List<BasicServiceInfo> listBasicInfoByModuleId(Long moduleId);
}
