package io.terminus.dalaran.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//@Component
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface HiddenParam {
}
