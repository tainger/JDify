package io.terminus.dalaran.core.component.annotation;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.model.BodyType;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Processor {
    String value();

    BodySerializeType inputSerializeType() default BodySerializeType.All;

    BodySerializeType outputSerializeType() default BodySerializeType.All;

    BodyType[] allowBodyTypes() default {};

    Class configType() default Void.class;

}
