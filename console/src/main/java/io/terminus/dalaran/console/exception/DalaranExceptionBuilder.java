package io.terminus.dalaran.console.exception;

import io.terminus.dalaran.exception.DalaranThrowable;

public final class DalaranExceptionBuilder<T extends DalaranThrowable> {

    private Class<T> exceptionClass;
    private String code;
    private String exceptionMessage;

    public static DalaranExceptionBuilder newBuilder() {
        return new DalaranExceptionBuilder();
    }

    public static <T extends DalaranThrowable> DalaranExceptionBuilder newBuilder(Class<T> exceptionClass) {
        return new DalaranExceptionBuilder().exception(exceptionClass);
    }

    public static <T extends DalaranThrowable> DalaranThrowable build(Class<T> exceptionClass) {
        return buildException(exceptionClass, null);
    }

    public static <T extends DalaranThrowable> T build(Class<T> exceptionClass, String code) {
        return buildException(exceptionClass, code);
    }

    public final DalaranExceptionBuilder exception(Class<T> exceptionClass) {
        this.exceptionClass = exceptionClass;
        return this;
    }

    public final DalaranExceptionBuilder code(String code) {
        this.code = code;
        return this;
    }

    public final DalaranExceptionBuilder exceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
        return this;
    }

    public final T build() {
        return buildException(this.exceptionClass, this.code);
    }

    private static <T extends DalaranThrowable> T buildException(Class<T> exceptionClass, String exceptionMessage) {
        try {
            T dalaranThrowable = exceptionClass.newInstance();
            dalaranThrowable.setMessage(exceptionMessage);
            return dalaranThrowable;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

}
