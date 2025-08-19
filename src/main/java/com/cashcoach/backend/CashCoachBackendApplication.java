package com.cashcoach.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CashCoachBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CashCoachBackendApplication.class, args);
	}

}
