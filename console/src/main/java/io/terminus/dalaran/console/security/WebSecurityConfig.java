package io.terminus.dalaran.console.security;

import io.terminus.dalaran.console.model.DalaranAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DalaranAccount account;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser(account.getUsername()).password(account.getPassword()).roles(account.getRole());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable().authorizeRequests()
//            .antMatchers("/api/platform/login/auth").permitAll()
//            .anyRequest().authenticated()
//            .and().httpBasic();
        http.csrf().disable()
            .authorizeRequests().antMatchers("/api/platform/login/auth").permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic();
    }
}
