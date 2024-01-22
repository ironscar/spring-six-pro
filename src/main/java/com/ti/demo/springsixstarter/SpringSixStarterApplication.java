package com.ti.demo.springsixstarter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.ti.demo.domain", "com.ti.demo.springsixstarter"})
public class SpringSixStarterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSixStarterApplication.class, args);
	}

}
