package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.DalaranAccount;
import io.terminus.draco.api.response.UserInfo;

import java.util.List;

public interface AuthorizeService {

    boolean authAccount(DalaranAccount account);

    UserInfo getUserInfo();
}
