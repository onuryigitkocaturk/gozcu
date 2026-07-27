package com.onuryigitkocaturk.query_monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class QueryMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueryMonitorApplication.class, args);
	}

}
