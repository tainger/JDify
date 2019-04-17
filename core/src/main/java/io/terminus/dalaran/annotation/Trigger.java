package io.terminus.dalaran.annotation;

import io.terminus.dalaran.BodyMode;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
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
