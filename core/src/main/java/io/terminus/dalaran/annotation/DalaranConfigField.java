package io.terminus.dalaran.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DalaranConfigField {
    String label() default "";

    FieldInputType inputType() default FieldInputType.Auto;
}
