package io.terminus.dalaran.open.common.annotation;


import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface OpenService {

    String channel();

    String service();

    Class inModel();

    Class outModel();
}
