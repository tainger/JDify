package io.terminus.dalaran;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DalaranIntegrationAction {

    String key();

    String name();

    String description() default "";

}
