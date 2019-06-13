package io.terminus.dalaran.component.trigger.scheduler;

import org.apache.camel.CamelContext;
import org.apache.camel.component.quartz2.QuartzComponent;
import org.apache.camel.util.IOHelper;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@Configuration
public class QuartzSpringConfiguration {

    // TODO 处理方式比较奇怪, 但是目前没有找到可以注入 properties 的方法
    public QuartzSpringConfiguration(CamelContext context) throws Exception {
        QuartzComponent component = new QuartzComponent();

        Properties answer = new Properties();
        InputStream is = StdSchedulerFactory.class.getClassLoader().getResourceAsStream("org/quartz/quartz.properties");
        try {
            answer.load(is);
            Properties envProperties = new Properties();
            envProperties.putAll(System.getenv());
            PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper(
                    PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX,
                    PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_SUFFIX,
                    PlaceholderConfigurerSupport.DEFAULT_VALUE_SEPARATOR, false
            );
            for (Map.Entry<Object, Object> property : answer.entrySet()) {
                String value = propertyPlaceholderHelper.replacePlaceholders((String) property.getValue(), envProperties);
                property.setValue(value);
            }
        } catch (IOException ignored) {
        } finally {
            IOHelper.close(is);
        }

        component.setProperties(answer);

        context.addComponent("quartz2", component);

        context.addStartupListener(component);
        component.setCamelContext(context);
        component.start();

    }
}
