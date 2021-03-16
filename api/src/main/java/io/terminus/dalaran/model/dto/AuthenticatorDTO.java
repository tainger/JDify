package io.terminus.dalaran.model.dto;

import io.terminus.dalaran.model.dto.basic.BasicAuthenticatorInfo;
import lombok.Data;

import java.util.List;

@Data
public class AuthenticatorDTO extends BasicAuthenticatorInfo {

    List<AuthenticatorConfigDTO> authenticator;
}
