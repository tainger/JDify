package io.terminus.dalaran.annotation;

import io.terminus.dalaran.BodyMode;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Trigger {
    String value();

    BodyMode bodyMode();

    boolean isVoid() default false;

    Class configType() default Object.class;

}
