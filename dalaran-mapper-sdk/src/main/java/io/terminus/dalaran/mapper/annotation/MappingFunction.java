package io.terminus.dalaran.mapper.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@IgnoreScan
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MappingFunction {

    String value();

    String description() default "";

    String function() default "execute";
}
