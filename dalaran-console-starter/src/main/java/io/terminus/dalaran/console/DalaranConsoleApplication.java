package io.terminus.dalaran.console;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author jiazhiyuan
 * @date 2023/7/2 5:34 PM
 */
@SpringBootApplication
@ComponentScan("io.terminus.dalaran")
public class DalaranConsoleApplication {
    public static void main(String[] args) {
        SpringApplication.run(DalaranConsoleApplication.class, args);
    }
}



    
