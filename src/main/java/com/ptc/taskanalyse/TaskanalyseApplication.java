package com.ptc.taskanalyse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TaskanalyseApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskanalyseApplication.class, args);
	}
}
