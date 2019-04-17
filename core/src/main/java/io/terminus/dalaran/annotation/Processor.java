package io.terminus.dalaran.annotation;

import io.terminus.dalaran.BodyMode;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Processor {
    String value();

    BodyMode bodyMode();

    Class configType() default Object.class;

}
