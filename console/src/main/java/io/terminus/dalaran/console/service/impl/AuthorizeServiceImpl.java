package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.service.AuthorizeService;
import io.terminus.dalaran.model.DalaranAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    @Autowired
    private DalaranAccount defaultAccount;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public boolean authAccount(DalaranAccount account) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(account.getUsername(), account.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList(""));
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return true;
    }
}
