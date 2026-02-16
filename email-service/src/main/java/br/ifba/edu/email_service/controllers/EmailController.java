package br.ifba.edu.email_service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Value;

import br.ifba.edu.email_service.dtos.EmailDTO;
import br.ifba.edu.email_service.service.EmailService;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Value("${server.port}")
    private String port;

    private EmailService service;

    public EmailController(EmailService service) {
        this.service = service;
    }

    @PostMapping("/send")
    public ResponseEntity<EmailDTO> sendEmail(@RequestBody EmailDTO data){
        return ResponseEntity.ok(service.sendEmail(data));
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Email Service is running in port: "+port);
    }
}
