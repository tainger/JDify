package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.OnException;
import io.terminus.dalaran.console.service.ClientManagementService;
import io.terminus.dalaran.model.dto.ClientDTO;
import io.terminus.dalaran.rest.read.ClientReadAPI;
import io.terminus.dalaran.rest.write.ClientWriteAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientManagementRest implements ClientReadAPI, ClientWriteAPI {

    @Autowired
    private ClientManagementService service;

    @Override
    @OnException(code = ResponseMessage.CLIENT_CREATE_ERROR)
    public Long create(@RequestBody ClientDTO clientDTO) {
        return service.create(clientDTO);
    }

    @Override
    @OnException(code = ResponseMessage.CLIENT_UPDATE_ERROR)
    public ClientDTO update(@RequestBody ClientDTO clientDTO) {
        return service.update(clientDTO);
    }

    @Override
    @OnException(code = ResponseMessage.CLIENT_DELETE_ERROR)
    public void deleteById(@PathVariable Long id) {
        service.delete(id);
    }

    @Override
    @OnException(code = ResponseMessage.CLIENT_QUERY_ERROR)
    public ClientDTO detail(@PathVariable Long id) {
        return service.detail(id);
    }
}
