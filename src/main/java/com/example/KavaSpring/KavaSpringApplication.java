package com.example.KavaSpring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KavaSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(KavaSpringApplication.class, args);
	}

}
