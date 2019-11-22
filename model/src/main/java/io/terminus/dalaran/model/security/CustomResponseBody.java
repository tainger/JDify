package io.terminus.dalaran.model.security;

import lombok.Data;

@Data
public class CustomResponseBody {

    private SecurityType type;

    private String message;
}
