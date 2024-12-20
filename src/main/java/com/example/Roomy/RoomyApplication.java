package com.example.Roomy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EntityScan(basePackages = {"com.my.jpa.entity"})
//@EnableJpaRepositories(basePackages = {"com.my.jpa.repository"})
@SpringBootApplication
public class RoomyApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoomyApplication.class, args);
	}

}
