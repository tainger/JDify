package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.OnException;
import io.terminus.dalaran.console.service.FunctionService;
import io.terminus.dalaran.model.dto.FunctionDTO;
import io.terminus.dalaran.rest.read.FunctionReadAPI;
import io.terminus.dalaran.rest.write.FunctionWriteAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FunctionRest implements FunctionReadAPI, FunctionWriteAPI {

    @Autowired
    private FunctionService service;

    @Override
    @OnException(code = ResponseMessage.FUNCTION_CREATE_ERROR)
    public Long create(@RequestBody FunctionDTO dto) {
        return service.create(dto);
    }

    @Override
    @OnException(code = ResponseMessage.FUNCTION_UPDATE_ERROR)
    public FunctionDTO update(@RequestBody FunctionDTO dto) {
        return service.update(dto);
    }

    @Override
    @OnException(code = ResponseMessage.FUNCTION_DELETE_ERROR)
    public void deleteById(@PathVariable Long id) {
        service.delete(id);
    }

    @Override
    @OnException(code = ResponseMessage.FUNCTION_QUERY_ERROR)
    public FunctionDTO detail(@PathVariable Long id) {
        return service.detail(id);
    }
}
