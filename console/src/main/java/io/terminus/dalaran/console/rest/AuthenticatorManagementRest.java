package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.OnException;
import io.terminus.dalaran.console.service.AuthenticatorService;
import io.terminus.dalaran.model.CreateResponse;
import io.terminus.dalaran.model.dto.AuthenticatorDTO;
import io.terminus.dalaran.rest.read.AuthenticatorReadAPI;
import io.terminus.dalaran.rest.write.AuthenticatorWriteAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticatorManagementRest implements AuthenticatorReadAPI, AuthenticatorWriteAPI {

    @Autowired
    private AuthenticatorService service;

    @Override
    @OnException(code = ResponseMessage.AUTHENTICATOR_CREATE_ERROR)
    public CreateResponse create(@RequestBody AuthenticatorDTO authenticatorDTO) {
        return new CreateResponse(service.create(authenticatorDTO));
    }

    @Override
    @OnException(code = ResponseMessage.AUTHENTICATOR_UPDATE_ERROR)
    public AuthenticatorDTO update(@RequestBody AuthenticatorDTO authenticatorDTO) {
        return service.update(authenticatorDTO);
    }

    @Override
    @OnException(code = ResponseMessage.AUTHENTICATOR_DELETE_ERROR)
    public void deleteById(@PathVariable String id) {
        service.delete(id);
    }

    @Override
    @OnException(code = ResponseMessage.AUTHENTICATORT_QUERY_ERROR)
    public AuthenticatorDTO detail(@PathVariable String id) {
        return service.detail(id);
    }
}
