package com.iyd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@MapperScan("com.iyd.mapper")
@EnableScheduling
public class IydApplication {
    public static void main(String[] args) {
        SpringApplication.run(IydApplication.class, args);
    }
}
