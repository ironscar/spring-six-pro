package com.ti.demo.springsixstarter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = {"com.ti.demo.domain", "com.ti.demo.springsixstarter"})
@EntityScan(basePackages = {"com.ti.demo.domain"})
public class SpringSixStarterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSixStarterApplication.class, args);
	}

}
