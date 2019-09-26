package io.terminus.dalaran.exception.flow;

import io.terminus.dalaran.exception.DalaranException;

public class FlowNotExistException extends DalaranException {
    public FlowNotExistException(String message) {
        super(message);
    }
}
