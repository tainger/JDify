package io.terminus.dalaran.core.component.annotation;

import io.terminus.dalaran.core.component.FieldInputType;

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

    boolean readonly() default false;

    FieldInputType inputType() default FieldInputType.Auto;

    Class connectorType() default Void.class;
}
