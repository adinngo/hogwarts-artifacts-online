package com.example.hogwarts_artifacts_online;

import com.example.hogwarts_artifacts_online.artifact.utils.IdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HogwartsArtifactsOnlineApplication {


	public static String baseUrl;
	public static void main(String[] args) {
		SpringApplication.run(HogwartsArtifactsOnlineApplication.class, args);


	}

	@Bean
	public IdWorker idWorker() {
		return new IdWorker(1,1);
	}

}
