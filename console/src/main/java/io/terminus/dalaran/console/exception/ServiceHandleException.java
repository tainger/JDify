package io.terminus.dalaran.console.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class ServiceHandleException extends RuntimeException {

    private String executeError;

    public ServiceHandleException(String message, String executeError) {
        super(message);
        this.executeError = executeError;
    }
}
