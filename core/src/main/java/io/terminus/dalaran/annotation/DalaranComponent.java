package io.terminus.dalaran.annotation;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DalaranComponent {
    String value();

    Class configType() default Object.class;
}
