package com.example.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class StoreApplication {

	@PostConstruct
	public void init(){
		// Setting Spring Boot SetTimeZone
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));
	}

	public static void main(String[] args) {
		SpringApplication.run(StoreApplication.class, args);
	}

}
