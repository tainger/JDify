package io.terminus.dalaran.core;


import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.context.DalaranModelTypeContext;
import io.terminus.dalaran.core.context.DalaranServiceContext;
import io.terminus.dalaran.core.flow.DalaranFragmentBuilder;
import io.terminus.dalaran.core.flow.DalaranNoticeBuilder;
import io.terminus.dalaran.core.flow.DalaranServiceBuilder;
import io.terminus.dalaran.core.flow.DalaranServiceLoader;
import io.terminus.dalaran.core.resource.*;
import io.terminus.dalaran.core.oss.*;
import io.terminus.dalaran.core.resource.property.PropertyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DalaranResourceConfiguration {

    @Bean
    public DalaranResourceBuilder dalaranResourceBuilder(
            DalaranResourceLoader resourceLoader,
            DalaranComponentContext componentContext,
            DalaranModelTypeContext converterContext,
            DalaranServiceContext serviceContext
    ) {
        return new DefaultDalaranResourceBuilder(resourceLoader, componentContext, converterContext, serviceContext);
    }

    @Bean
    public DalaranFragmentBuilder fragmentBuilder(DalaranResourceBuilder resourceBuilder) {
        return new DefaultDalaranFragmentBuilder(resourceBuilder);
    }

    @Bean
    public DalaranServiceLoader serviceLoader(DalaranResourceLoader resourceLoader) {
        return new DefaultDalaranServiceLoader(resourceLoader);
    }

    @Bean
    public DalaranServiceBuilder serviceBuilder(DalaranResourceBuilder resourceBuilder) {
        return new DefaultDalaranServiceBuilder(resourceBuilder);
    }

    @Bean
    public OSSAccount ossAccount(@Value("${oss.endpoint}") String endpoint, @Value("${oss.accessId}") String accessId, @Value("${oss.accessSecret}") String accessSecret, @Value("${oss.bucketName}") String bucketName, @Value("${oss.rootDir}") String rootDir) {
        return new OSSAccount(endpoint, accessId, accessSecret, bucketName, rootDir);
    }
}