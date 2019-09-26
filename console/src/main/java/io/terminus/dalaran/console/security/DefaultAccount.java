package io.terminus.dalaran.console.security;

import io.terminus.dalaran.model.DalaranAccount;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class DefaultAccount {

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @Value("${spring.security.user.role}")
    private String role;

    @Bean
    public DalaranAccount init() {
        return new DalaranAccount(username, password, role);
    }
}
