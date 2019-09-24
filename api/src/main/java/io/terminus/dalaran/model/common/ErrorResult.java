package io.terminus.dalaran.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResult {

    private String message;

    private boolean successful;

    public static ErrorResult successful() {
        return successful(null);
    }

    public static ErrorResult successful(String message) {
        ErrorResult result = new ErrorResult();
        result.setSuccessful(true);
        result.setMessage(message);
        return result;
    }

    public static ErrorResult error(String message) {
        return new ErrorResult(message, false);
    }
}
