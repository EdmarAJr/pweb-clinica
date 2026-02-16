package br.ifba.edu.clinica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;
import io.github.cdimascio.dotenv.Dotenv;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableScheduling
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class ClinicaApplication {

	public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("RABBITMQ_HOST", dotenv.get("RABBITMQ_HOST"));
        System.setProperty("RABBITMQ_PORT", dotenv.get("RABBITMQ_PORT"));
        System.setProperty("RABBITMQ_USERNAME", dotenv.get("RABBITMQ_USERNAME"));
        System.setProperty("RABBITMQ_PASSWORD", dotenv.get("RABBITMQ_PASSWORD"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));


		SpringApplication.run(ClinicaApplication.class, args);
	}

}