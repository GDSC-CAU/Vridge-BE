package com.gdsc.vridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class VridgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(VridgeApplication.class, args);
	}

}
