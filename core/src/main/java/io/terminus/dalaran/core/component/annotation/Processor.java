package io.terminus.dalaran.core.component.annotation;

import io.terminus.dalaran.DalaranConstants;
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
     *
     * @return
     */
    String[] value();

    int order() default Short.MAX_VALUE;

    String bodyType() default DalaranConstants.OBJECT_MODEL_TYPE;

    Class configType() default Void.class;

    String developer() default DalaranConstants.PARTNER;

    String description() default DalaranConstants.DALARAN_COMPONENT_DEFAULT_DESC;
}
