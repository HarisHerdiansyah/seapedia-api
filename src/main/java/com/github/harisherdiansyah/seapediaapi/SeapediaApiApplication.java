package com.github.harisherdiansyah.seapediaapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SeapediaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeapediaApiApplication.class, args);
    }

}
