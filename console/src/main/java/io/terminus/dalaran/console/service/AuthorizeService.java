package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.DalaranAccount;

public interface AuthorizeService {

    boolean authAccount(DalaranAccount account);
}
