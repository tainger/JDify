package io.terminus.dalaran.core.component.annotation;

import io.terminus.dalaran.model.DalaranModelSchema;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ModelType {
    String value();

    Class<? extends DalaranModelSchema> modelSchema();
}
