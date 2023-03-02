package com.myykk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.myykk"})
@PropertySource({"classpath:Queries.xml"})
@EnableScheduling
public class SchedulingWingsDailyJobApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulingWingsDailyJobApplication.class);
	}
}
