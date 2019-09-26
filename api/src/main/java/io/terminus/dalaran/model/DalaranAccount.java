package io.terminus.dalaran.model;

import lombok.Data;

@Data
public class DalaranAccount {

    private String username;

    private String password;

    private String role;

    public DalaranAccount() {
    }

    public DalaranAccount(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
