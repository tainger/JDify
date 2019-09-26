package io.terminus.dalaran.exception.model;

import io.terminus.dalaran.exception.DalaranException;

public class ModelNotExistException extends DalaranException {
    public ModelNotExistException(String message) {
        super(message);
    }
}
