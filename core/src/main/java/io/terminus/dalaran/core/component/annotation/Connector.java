package io.terminus.dalaran.core.component.annotation;

import io.terminus.dalaran.DalaranConstants;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Connector {
    String value();

    int order() default Short.MAX_VALUE;

    String description() default DalaranConstants.DALARAN_COMPONENT_DEFAULT_DESC;
}
