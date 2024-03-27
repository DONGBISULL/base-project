package com.demo.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.demo.modules.*" , "com.demo.api.*"})
@EnableJpaRepositories("com.demo.modules.repository") // JPA Repository 패키지 경로 지정
@EntityScan("com.demo.modules.entity") // 엔티티 클래스 패키지 경로 지정
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
