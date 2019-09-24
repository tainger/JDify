package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.api.rest.FunctionRestAPI;
import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.DalaranException;
import io.terminus.dalaran.console.service.FunctionService;
import io.terminus.dalaran.model.dto.FunctionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FunctionRest implements FunctionRestAPI {

    @Autowired
    private FunctionService service;

    @Override
    @DalaranException(value = ResponseMessage.FUNCTION_CREATE_ERROR)
    public Long create(@RequestBody FunctionDTO dto) {
        return service.create(dto);
    }

    @Override
    @DalaranException(value = ResponseMessage.FUNCTION_UPDATE_ERROR)
    public FunctionDTO update(@RequestBody FunctionDTO dto) {
        return service.update(dto);
    }

    @Override
    @DalaranException(value = ResponseMessage.FUNCTION_DELETE_ERROR)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @Override
    @DalaranException(value = ResponseMessage.FUNCTION_QUERY_ERROR)
    public FunctionDTO detail(@PathVariable Long id) {
        return service.detail(id);
    }
}
