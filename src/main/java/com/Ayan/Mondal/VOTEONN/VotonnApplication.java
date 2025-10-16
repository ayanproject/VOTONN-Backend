package com.Ayan.Mondal.VOTEONN;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.Ayan.Mondal.VOTEONN.REPOSITORY") // <--- Add this
public class VotonnApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotonnApplication.class, args);
	}
}
