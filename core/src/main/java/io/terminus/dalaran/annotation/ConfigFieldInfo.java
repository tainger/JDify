package io.terminus.dalaran.annotation;

import io.terminus.dalaran.FieldInputType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ConfigFieldInfo {

    String defaultValue() default "";

    String example() default "";

    String label() default "";

    boolean required() default true;

    FieldInputType inputType() default FieldInputType.Auto;

    boolean isEnum() default false;
}
