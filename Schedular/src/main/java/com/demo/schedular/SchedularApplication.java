package com.demo.schedular;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SchedularApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedularApplication.class, args);
    }

}
