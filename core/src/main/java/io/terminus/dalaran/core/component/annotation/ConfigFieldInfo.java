package io.terminus.dalaran.core.component.annotation;

import io.terminus.dalaran.FieldInputType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ConfigFieldInfo {

    String sourceType() default "";

    String defaultValue() default "";

    String example() default "";

    String label() default "";

    boolean required() default true;

    boolean readonly() default false;

    String path() default "";

    String param() default "";

    String show() default "";

    String inputType() default FieldInputType.Auto;

    Class connectorType() default Void.class;

    Class limiterType() default Void.class;

    Class authenticatorType() default Void.class;
}
