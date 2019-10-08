package io.terminus.dalaran.exception;

import io.terminus.dalaran.model.common.ErrorMessage;

public interface DalaranThrowable {

    void setErrorMessage(ErrorMessage errorMessage);

    void setMessage(String message);
}
