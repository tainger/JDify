package io.terminus.dalaran.annotation;

import io.terminus.dalaran.BodyMode;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Processor {
    String value();

    BodyMode bodyMode();

    Class configType() default Object.class;

}
