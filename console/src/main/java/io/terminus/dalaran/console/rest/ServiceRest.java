package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.OnException;
import io.terminus.dalaran.console.service.ServiceManagement;
import io.terminus.dalaran.model.component.ServiceOperation;
import io.terminus.dalaran.model.dto.ServiceDTO;
import io.terminus.dalaran.rest.read.ServiceReadAPI;
import io.terminus.dalaran.rest.write.ServiceWriteAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ServiceRest implements ServiceReadAPI, ServiceWriteAPI {

    @Autowired
    private ServiceManagement serviceManagement;

    @Override
    @OnException(code = ResponseMessage.SERVICE_CREATE_ERROR)
    public Long create(@RequestBody ServiceDTO serviceDTO) {
        return serviceManagement.create(serviceDTO);
    }

    @Override
    @OnException(code = ResponseMessage.SERVICE_UPDATE_ERROR)
    public ServiceDTO update(@RequestBody ServiceDTO serviceDTO) {
        return serviceManagement.update(serviceDTO);
    }

    @Override
    @OnException(code = ResponseMessage.SERVICE_DELETE_ERROR)
    public void deleteById(@PathVariable Long id) {
        serviceManagement.delete(id);
    }

    @Override
    @OnException(code = ResponseMessage.SERVICE_QUERY_ERROR)
    public ServiceDTO detail(@PathVariable Long id) {
        return serviceManagement.detail(id);
    }

    @Override
    @OnException(code = ResponseMessage.SERVICE_QUERY_ERROR)
    public List<ServiceDTO> list() {
        return serviceManagement.list();
    }

    @Override
    @OnException(code = ResponseMessage.SERVICE_QUERY_ERROR)
    public List<ServiceOperation> operations(@PathVariable Long id) {
        return serviceManagement.listOperation(id);
    }
}
