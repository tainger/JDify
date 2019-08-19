package io.terminus.dalaran.console.model;

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
        ErrorResult result = new ErrorResult();
        result.setSuccessful(true);
        return result;
    }

    public static ErrorResult error(String message) {
        return new ErrorResult(message, false);
    }
}
