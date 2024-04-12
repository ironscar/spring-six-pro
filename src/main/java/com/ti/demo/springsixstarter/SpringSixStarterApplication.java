package com.ti.demo.springsixstarter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.ti.demo.domain", "com.ti.demo.springsixstarter"})
@MapperScan(basePackages = {"com.ti.demo.springsixstarter.dao.mybatis.xmlsql"})
public class SpringSixStarterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSixStarterApplication.class, args);
	}

}
