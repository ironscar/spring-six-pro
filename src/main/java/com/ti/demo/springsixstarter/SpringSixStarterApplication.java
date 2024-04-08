package com.ti.demo.springsixstarter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.ti.demo.domain", "com.ti.demo.springsixstarter"})
@MapperScan(basePackages = {"com.ti.demo.springsixstarter.dao.mybatis.xmlsql"})
public class SpringSixStarterApplication {

	@Value("${spring.datasource.url}")
	static String datasourceUrl;

	@Value("${spring.datasource.username}")
	static String datasourceUser;

	@Value("${spring.datasource.password}")
	static String datasourcePass;

	public static void main(String[] args) {
		SpringApplication.run(SpringSixStarterApplication.class, args);
		log.info("---------------- {}, {}, {} ----------------", datasourceUrl, datasourceUser, datasourcePass);
	}

}
