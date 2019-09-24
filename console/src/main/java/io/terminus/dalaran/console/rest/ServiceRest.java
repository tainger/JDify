package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.api.rest.ServiceRestAPI;
import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.DalaranException;
import io.terminus.dalaran.console.service.ServiceManagement;
import io.terminus.dalaran.model.dto.ServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ServiceRest implements ServiceRestAPI {

    @Autowired
    private ServiceManagement serviceManagement;

    @Override
    @DalaranException(value = ResponseMessage.SERVICE_CREATE_ERROR)
    public Long create(@RequestBody ServiceDTO serviceDTO) {
        return serviceManagement.create(serviceDTO);
    }

    @Override
    @DalaranException(value = ResponseMessage.SERVICE_UPDATE_ERROR)
    public ServiceDTO update(@RequestBody ServiceDTO serviceDTO) {
        return serviceManagement.update(serviceDTO);
    }

    @Override
    @DalaranException(value = ResponseMessage.SERVICE_DELETE_ERROR)
    public void create(@PathVariable Long id) {
        serviceManagement.delete(id);
    }

    @Override
    @DalaranException(value = ResponseMessage.SERVICE_QUERY_ERROR)
    public ServiceDTO detail(@PathVariable Long id) {
        return serviceManagement.detail(id);
    }

    @Override
    @DalaranException(value = ResponseMessage.SERVICE_QUERY_ERROR)
    public List<ServiceDTO> list() {
        return serviceManagement.list();
    }

    @Override
    @DalaranException(value = ResponseMessage.SERVICE_QUERY_ERROR)
    public List<String> operations(@PathVariable Long id) {
        return serviceManagement.listOperation(id);
    }
}
