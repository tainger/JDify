package io.terminus.dalaran;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DalaranIntegration {

    String key();

    String name();

    String description() default "";

}
