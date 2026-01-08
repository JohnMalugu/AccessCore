package com.jcmlabs.AccessCore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.jcmlabs.AccessCore")
public class AccessCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccessCoreApplication.class, args);
	}

}
