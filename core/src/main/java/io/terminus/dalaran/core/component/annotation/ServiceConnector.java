package io.terminus.dalaran.core.component.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ServiceConnector {
    String value();

    Class importConfigType();

    Class serviceConfigType();

}
