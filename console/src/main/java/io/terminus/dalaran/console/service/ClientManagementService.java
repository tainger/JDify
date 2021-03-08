package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.dto.ClientDTO;
import io.terminus.dalaran.model.dto.basic.BasicClientInfo;

import java.util.List;

public interface ClientManagementService {

    String create(ClientDTO clientDTO);

    ClientDTO update(ClientDTO clientDTO);

    void delete(String appKey);

    ClientDTO detail(String appKey);

    List<BasicClientInfo> listBasicInfoByModuleId(String moduleId);
}
