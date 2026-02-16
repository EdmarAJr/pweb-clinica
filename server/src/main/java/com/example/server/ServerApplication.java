package com.example.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableEurekaServer
public class ServerApplication {

	public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();
        System.setProperty("PORT", dotenv.get("PORT"));
        System.setProperty("URL", dotenv.get("URL"));

        SpringApplication.run(ServerApplication.class, args);
	}

}
