package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.dto.AuthenticatorDTO;
import io.terminus.dalaran.model.dto.basic.BasicAuthenticatorInfo;

import java.util.List;

public interface AuthenticatorService {

    String create(AuthenticatorDTO authenticatorDTO);

    AuthenticatorDTO update(AuthenticatorDTO authenticatorDTO);

    void delete(String authenticatorId);

    AuthenticatorDTO detail(String authenticatorId);

    List<BasicAuthenticatorInfo> listBasicInfoByModuleId(String moduleId);
}
