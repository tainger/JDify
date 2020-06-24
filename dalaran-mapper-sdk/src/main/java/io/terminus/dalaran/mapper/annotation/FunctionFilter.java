package io.terminus.dalaran.mapper.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Component
@IgnoreScan
@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionFilter {
}
