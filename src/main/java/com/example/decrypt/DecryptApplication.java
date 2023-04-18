package com.example.decrypt;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DecryptApplication {

    public static void main(String[] args) {
        SpringApplication.run(DecryptApplication.class, args);
    }

    @Bean
    public CommandLineRunner saveFromDocumentStorage(DecryptAndSaveService decryptAndSaveService) {
        return args -> {
            decryptAndSaveService.saveConsumerRecord();
        };
    }
}
