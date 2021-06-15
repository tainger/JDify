package io.terminus.dalaran.component.authenticator;

import lombok.Data;

import java.util.List;

@Data
public class DalaranAuthenticator<T> {

    private String type;

    private List<T> config;

}
