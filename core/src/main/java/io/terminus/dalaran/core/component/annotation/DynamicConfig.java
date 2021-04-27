package io.terminus.dalaran.core.component.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DynamicConfig {

    String value();

    String name();

    String type();

    String origin();

    String version() default "";

}
