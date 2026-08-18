package com.digimon.dtskrB;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DtskrBApplication {

	public static void main(String[] args) {
		SpringApplication.run(DtskrBApplication.class, args);
	}

}
