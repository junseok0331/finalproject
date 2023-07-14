package com.small.group;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EzenFinalProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(EzenFinalProjectApplication.class, args);
	}

}
