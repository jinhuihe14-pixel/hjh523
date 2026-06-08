package com.arttraining.business;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.arttraining.business", "com.arttraining.common"})
@MapperScan("com.arttraining.business.mapper")
@EnableScheduling
public class BusinessCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessCenterApplication.class, args);
    }
}
