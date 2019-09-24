package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.api.rest.ClientRestAPI;
import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.DalaranException;
import io.terminus.dalaran.console.service.ClientManagementService;
import io.terminus.dalaran.model.dto.ClientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientManagementRest implements ClientRestAPI {

    @Autowired
    private ClientManagementService service;

    @Override
    @DalaranException(value = ResponseMessage.CLIENT_CREATE_ERROR)
    public Long create(@RequestBody ClientDTO clientDTO) {
        return service.create(clientDTO);
    }

    @Override
    @DalaranException(value = ResponseMessage.CLIENT_UPDATE_ERROR)
    public ClientDTO update(@RequestBody ClientDTO clientDTO) {
        return service.update(clientDTO);
    }

    @Override
    @DalaranException(value = ResponseMessage.CLIENT_DELETE_ERROR)
    public void create(@PathVariable Long id) {
        service.delete(id);
    }

    @Override
    @DalaranException(value = ResponseMessage.CLIENT_QUERY_ERROR)
    public ClientDTO detail(@PathVariable Long id) {
        return service.detail(id);
    }
}
