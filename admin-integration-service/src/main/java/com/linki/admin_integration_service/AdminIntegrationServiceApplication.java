package com.linki.admin_integration_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "com.linki")
@EntityScan("com.linki.admin_integration_service.entity")
public class AdminIntegrationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminIntegrationServiceApplication.class, args);
	}

}
