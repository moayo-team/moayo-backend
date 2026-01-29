package com.moayo.moayobackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MoayoBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoayoBackendApplication.class, args);
    }

}
