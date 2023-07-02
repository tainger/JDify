package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.TenantInfo;
import io.terminus.dalaran.model.DalaranAccount;
import io.terminus.dalaran.model.user.UserInfo;

public interface AuthorizeService {

    boolean authAccount(DalaranAccount account);

    UserInfo getUserInfo();

    TenantInfo getCurrentTenant();
}
