package io.terminus.dalaran.annotation;

import io.terminus.dalaran.BodyMode;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Component {
    String value();

    BodyMode bodyMode() default BodyMode.Serialized;

    Class configType() default Object.class;

}
