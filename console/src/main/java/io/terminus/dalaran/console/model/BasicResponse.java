package io.terminus.dalaran.console.model;


public class BasicResponse {

    private boolean success;

    private Object result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "BasicResponse{" +
                "success=" + success +
                ", result=" + result +
                '}';
    }
}
