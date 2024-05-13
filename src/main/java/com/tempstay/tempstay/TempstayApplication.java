package com.tempstay.tempstay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TempstayApplication {

	public static void main(String[] args) {
		SpringApplication.run(TempstayApplication.class, args);
	}

}
