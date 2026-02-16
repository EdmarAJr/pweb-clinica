package br.ifba.edu.email_service.dtos;

import br.ifba.edu.email_service.entities.Email;

public record EmailDTO(String mailFrom, String mailTo, String mailSubject, String mailText) {
    public EmailDTO(Email email) {
        this(email.getMailFrom(),email.getMailTo(), email.getMailSubject(),email.getMailText());
    }
}