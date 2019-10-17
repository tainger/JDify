package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.dto.ClientDTO;
import io.terminus.dalaran.model.dto.basic.BasicClientInfo;

import java.util.List;

public interface ClientManagementService {

    Long create(ClientDTO clientDTO);

    ClientDTO update(ClientDTO clientDTO);

    void delete(Long appKey);

    ClientDTO detail(Long appKey);

    List<BasicClientInfo> listBasicInfoByModuleId(Long moduleId);
}
