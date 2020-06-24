package io.terminus.dalaran.model.annotation;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MappingFunction {

    String value();

    String description() default "";

    String function() default "execute";
}
