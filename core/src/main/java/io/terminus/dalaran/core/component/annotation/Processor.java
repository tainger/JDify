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
    /**
     * processor type, 第一个会作为主 type, 其他可以认为是曾用名, connector 的数据不通用
     * @return
     */
    String[] value();

    String name();

    int order() default Short.MAX_VALUE;

    BodySerializeType inputSerializeType() default BodySerializeType.All;

    BodySerializeType outputSerializeType() default BodySerializeType.All;

    BodyType[] allowBodyTypes() default {};

    Class configType() default Void.class;

}
