package com.ywy.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class YwyStudyApplication {
    public static void main(String[] args) {
        SpringApplication.run(YwyStudyApplication.class, args);
    }
}