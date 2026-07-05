package com.subastaYa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SubastaYaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SubastaYaApplication.class, args);
    }
}