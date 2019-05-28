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
public @interface Trigger {
    String value();

    BodySerializeType serializeType() default BodySerializeType.Serialized;

    BodyType[] allowBodyTypes() default {};

    boolean isVoid() default false;

    Class configType() default Void.class;

}
