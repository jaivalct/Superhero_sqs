package com.cleartax.training_superheroes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableSqs
public class TrainingSuperheroesApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainingSuperheroesApplication.class, args);
	}

}
