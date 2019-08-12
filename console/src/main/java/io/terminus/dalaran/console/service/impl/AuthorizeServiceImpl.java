package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.model.DalaranAccount;
import io.terminus.dalaran.console.service.AuthorizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    @Autowired
    private DalaranAccount defaultAccount;

    @Override
    public boolean authAccount(DalaranAccount account) {
        return account.getUsername().equals(defaultAccount.getUsername()) && account.getPassword().equals(defaultAccount.getPassword());
    }
}
