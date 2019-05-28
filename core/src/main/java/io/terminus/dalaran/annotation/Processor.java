package io.terminus.dalaran.annotation;

import io.terminus.dalaran.BodySerializeType;
import io.terminus.dalaran.BodyType;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Processor {
    String value();

    BodySerializeType serializeType() default BodySerializeType.Object;

    BodyType[] allowBodyTypes() default {};

    Class configType() default Void.class;

}
