package io.terminus.dalaran.console;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import io.terminus.dalaran.configura.DalaranAutoConfigura;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@SpringBootApplication(scanBasePackages = {"io.terminus.dalaran"})
@EnableSwagger2
@EntityScan(basePackages = {"io.terminus.dalaran"})
@EnableJpaRepositories(basePackages = {"io.terminus.dalaran"})
@Import(DalaranAutoConfigura.class)
public class ConsoleApplication extends WebMvcConfigurerAdapter {

    @Bean
    public TestFlowLoader testFlowLoader() {
        return new TestFlowLoader();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        converters.add(fastConverter);
    }


    public static void main(String[] args) {
        SpringApplication.run(ConsoleApplication.class);
    }
}
