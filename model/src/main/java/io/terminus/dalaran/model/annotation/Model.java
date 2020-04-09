package io.terminus.dalaran.model.annotation;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Model {
    String value();

    int order() default Short.MAX_VALUE;
}
