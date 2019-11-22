package io.terminus.dalaran.console.security.basic;

import io.terminus.dalaran.model.DalaranAccount;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class DalaranUserDetailsService implements UserDetailsService {

    @Autowired
    private DalaranAccount defaultAccount;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String defaultUsername = defaultAccount.getUsername();
        if (StringUtils.equals(defaultUsername, username)) {
            Collection<GrantedAuthority> authList = getAuthorities();
            return new User(username, defaultAccount.getPassword(), true, true, true, true, authList);
        }
        return null;
    }

    private Collection<GrantedAuthority> getAuthorities(){
        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
        authList.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authList;
    }
}
