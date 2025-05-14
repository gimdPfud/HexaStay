package com.sixthsense.hexastay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SixthsenseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SixthsenseApplication.class, args);
    }

}
