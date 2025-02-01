package com.cgtech.apifile;

import com.cgtech.apifile.config.StorageProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperty.class)
@EnableScheduling
public class ApifileApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApifileApplication.class, args);
	}

}
