package io.terminus.dalaran.console.exception;

import io.terminus.dalaran.exception.DalaranException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnException {

    String message();

    Class<? extends DalaranException> exception() default DalaranException.class;
}
