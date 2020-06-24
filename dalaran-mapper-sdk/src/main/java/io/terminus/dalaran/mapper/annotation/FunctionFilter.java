package io.terminus.dalaran.mapper.annotation;

import io.terminus.dalaran.model.annotation.IgnoreScan;
import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Component
@IgnoreScan
@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionFilter {
}
