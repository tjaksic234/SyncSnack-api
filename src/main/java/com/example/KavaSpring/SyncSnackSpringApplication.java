package com.example.KavaSpring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableMongoAuditing
@Slf4j
public class SyncSnackSpringApplication {
	public static void main(String[] args) {
		SpringApplication.run(SyncSnackSpringApplication.class, args);
	}
}
