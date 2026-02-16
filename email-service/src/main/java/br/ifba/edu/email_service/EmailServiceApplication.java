package br.ifba.edu.email_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmailServiceApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
        System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
        System.setProperty("RABBITMQ_HOST", dotenv.get("RABBITMQ_HOST"));
        System.setProperty("RABBITMQ_PORT", dotenv.get("RABBITMQ_PORT"));
        System.setProperty("RABBITMQ_USERNAME", dotenv.get("RABBITMQ_USERNAME"));
        System.setProperty("RABBITMQ_PASSWORD", dotenv.get("RABBITMQ_PASSWORD"));

        SpringApplication.run(EmailServiceApplication.class, args);
    }

}