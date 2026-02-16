package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();
        System.setProperty("PORT", dotenv.get("PORT"));
        System.setProperty("URL", dotenv.get("URL"));

        SpringApplication.run(GatewayApplication.class, args);

	}

}
