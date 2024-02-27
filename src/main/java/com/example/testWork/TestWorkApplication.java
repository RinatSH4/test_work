package com.example.testWork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TestWorkApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestWorkApplication.class, args);
	}

}
