package io.terminus.dalaran.core.component.annotation;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Limiter {
    String value();

    int order() default Short.MAX_VALUE;
}
