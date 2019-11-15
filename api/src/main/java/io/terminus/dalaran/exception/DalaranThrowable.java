package io.terminus.dalaran.exception;

import io.terminus.dalaran.model.common.ErrorMessage;
import org.jetbrains.annotations.NotNull;

public interface DalaranThrowable {

    default void setErrorMessage(@NotNull ErrorMessage errorMessage) {
        this.setMessage(errorMessage.getExceptionMessage());
    }

    void setMessage(String message);

    String getCode();
}
