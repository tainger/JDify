package io.terminus.dalaran.rest;

import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients
public class DalaranClientConfigure {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new DalaranCustomErrorDecoder();
    }
}
