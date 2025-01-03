package com.huawei.it.euler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableTransactionManagement
@EnableWebMvc
@EnableRetry
@EnableScheduling
@EnableAsync
public class OpenEulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenEulerApplication.class, args);
    }

}
