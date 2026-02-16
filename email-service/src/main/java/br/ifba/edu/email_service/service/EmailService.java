package br.ifba.edu.email_service.service;

import java.time.LocalDateTime;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.ifba.edu.email_service.dtos.EmailDTO;
import br.ifba.edu.email_service.dtos.DadosConsultaDTO;
import br.ifba.edu.email_service.dtos.DadosNotificacaoMedicoDTO;
import br.ifba.edu.email_service.entities.Email;
import br.ifba.edu.email_service.entities.EmailStatus;
import br.ifba.edu.email_service.repositories.EmailRepository;

@Service
public class EmailService {
    private EmailRepository emailRepository;
    private JavaMailSender emailSender;

    public EmailService(EmailRepository emailRepository, JavaMailSender emailSender) {
        this.emailRepository = emailRepository;
        this.emailSender = emailSender;
    }

    public EmailDTO sendEmail(EmailDTO emaildto) {
        Email novoEmail=new Email(emaildto);
        novoEmail.setSendDateEmail(LocalDateTime.now());
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(emaildto.mailFrom());
        message.setTo(emaildto.mailTo());
        message.setSubject(emaildto.mailSubject());
        message.setText(emaildto.mailText());

        try {
            emailSender.send(message);
            novoEmail.setStatus(EmailStatus.SENT);
        } catch (MailException e) {
            novoEmail.setStatus(EmailStatus.ERROR);
            throw e;
        } finally {
            emailRepository.save(novoEmail);
        }
        
        return new EmailDTO(novoEmail);
    }

    public DadosConsultaDTO sendEmail(DadosConsultaDTO consultadto) {
        // Envio para o Paciente
        if (consultadto.emailPaciente() != null) {
            if ("AGENDADA".equals(consultadto.statusConsulta())) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("darkspider360@gmail.com");
                message.setTo(consultadto.emailPaciente());
                message.setSubject("Agendamento de consulta");
                message.setText("Olá, " + consultadto.nomePaciente() + ".\nEstamos confirmando a sua consulta marcada com a(o) " + consultadto.nomeMedico() + " para a data " + consultadto.dataHora() + ". \nPor favor, chegue com 30 minutos de antecedência");

                Email emailLog = new Email();
                emailLog.setMailFrom(message.getFrom());
                emailLog.setMailTo(consultadto.emailPaciente());
                emailLog.setMailSubject(message.getSubject());
                emailLog.setMailText(message.getText());
                emailLog.setSendDateEmail(LocalDateTime.now());

                try {
                    emailSender.send(message);
                    emailLog.setStatus(EmailStatus.SENT);
                } catch (MailException e) {
                    emailLog.setStatus(EmailStatus.ERROR);
                    throw e;
                } finally {
                    emailRepository.save(emailLog);
                }
            }

            if("REAGENDADA".equals(consultadto.statusConsulta())){
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("darkspider360@gmail.com");
                message.setTo(consultadto.emailPaciente());
                message.setSubject("Reagendamento de consulta");
                message.setText("Olá, " + consultadto.nomePaciente() + ".\nEstamos confirmando a remarcação da sua consulta marcada com a(o) " + consultadto.nomeMedico() + " para a data " + consultadto.dataHora() + ". \nPor favor, chegue com 30 minutos de antecedência");

                Email emailLog = new Email();
                emailLog.setMailFrom(message.getFrom());
                emailLog.setMailTo(consultadto.emailPaciente());
                emailLog.setMailSubject(message.getSubject());
                emailLog.setMailText(message.getText());
                emailLog.setSendDateEmail(LocalDateTime.now());

                try {
                    emailSender.send(message);
                    emailLog.setStatus(EmailStatus.SENT);
                } catch (MailException e) {
                    emailLog.setStatus(EmailStatus.ERROR);
                    throw e;
                } finally {
                    emailRepository.save(emailLog);
                }
            }
        }

        // Envio para o Médico
        if (consultadto.emailMedico() != null) {
            if("AGENDADA".equals(consultadto.statusConsulta())){
                SimpleMailMessage messageMedico = new SimpleMailMessage();
                messageMedico.setFrom("darkspider360@gmail.com");
                messageMedico.setTo(consultadto.emailMedico());
                messageMedico.setSubject("Nova consulta agendada");
                messageMedico.setText("Olá, " + consultadto.nomeMedico() + ".\nUma nova consulta foi agendada com o paciente " + consultadto.nomePaciente() + " para a data " + consultadto.dataHora() + ".");

                Email emailLogMedico = new Email();
                emailLogMedico.setMailFrom(messageMedico.getFrom());
                emailLogMedico.setMailTo(consultadto.emailMedico());
                emailLogMedico.setMailSubject(messageMedico.getSubject());
                emailLogMedico.setMailText(messageMedico.getText());
                emailLogMedico.setSendDateEmail(LocalDateTime.now());

                try {
                    emailSender.send(messageMedico);
                    emailLogMedico.setStatus(EmailStatus.SENT);
                } catch (MailException e) {
                    emailLogMedico.setStatus(EmailStatus.ERROR);
                    throw e;
                } finally {
                    emailRepository.save(emailLogMedico);
                }
            }

            if("REAGENDADA".equals(consultadto.statusConsulta())){
                SimpleMailMessage messageMedico = new SimpleMailMessage();
                messageMedico.setFrom("darkspider360@gmail.com");
                messageMedico.setTo(consultadto.emailMedico());
                messageMedico.setSubject("Consulta reagendada");
                messageMedico.setText("Olá, " + consultadto.nomeMedico() + ".\nA consulta com o paciente " + consultadto.nomePaciente() + " para a data " + consultadto.dataHora() + " foi reagendada.");

                Email emailLogMedico = new Email();
                emailLogMedico.setMailFrom(messageMedico.getFrom());
                emailLogMedico.setMailTo(consultadto.emailMedico());
                emailLogMedico.setMailSubject(messageMedico.getSubject());
                emailLogMedico.setMailText(messageMedico.getText());
                emailLogMedico.setSendDateEmail(LocalDateTime.now());

                try {
                    emailSender.send(messageMedico);
                    emailLogMedico.setStatus(EmailStatus.SENT);
                } catch (MailException e) {
                    emailLogMedico.setStatus(EmailStatus.ERROR);
                    throw e;
                } finally {
                    emailRepository.save(emailLogMedico);
                }
            }

            if("CANCELADA".equals(consultadto.statusConsulta())){
                SimpleMailMessage messageMedico = new SimpleMailMessage();
                messageMedico.setFrom("darkspider360@gmail.com");
                messageMedico.setTo(consultadto.emailMedico());
                messageMedico.setSubject("Consulta cancelada");
                messageMedico.setText("Olá, " + consultadto.nomeMedico() + ".\nA consulta com o paciente " + consultadto.nomePaciente() + " para a data " + consultadto.dataHora() + " foi cancelada.");

                Email emailLogMedico = new Email();
                emailLogMedico.setMailFrom(messageMedico.getFrom());
                emailLogMedico.setMailTo(consultadto.emailMedico());
                emailLogMedico.setMailSubject(messageMedico.getSubject());
                emailLogMedico.setMailText(messageMedico.getText());
                emailLogMedico.setSendDateEmail(LocalDateTime.now());

                try {
                    emailSender.send(messageMedico);
                    emailLogMedico.setStatus(EmailStatus.SENT);
                } catch (MailException e) {
                    emailLogMedico.setStatus(EmailStatus.ERROR);
                    throw e;
                } finally {
                    emailRepository.save(emailLogMedico);
                }
            }
        }

        return consultadto;
    }

    public void sendEmail(DadosNotificacaoMedicoDTO medicoDto) {
        if (medicoDto.emailMedico() != null) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("darkspider360@gmail.com");
            message.setTo(medicoDto.emailMedico());
            message.setSubject("Cadastro ativado");
            message.setText("Olá, " + medicoDto.nomeMedico() + ".\nSeu cadastro foi ativado com sucesso em nossa plataforma.");

            Email emailLog = new Email();
            emailLog.setMailFrom(message.getFrom());
            emailLog.setMailTo(medicoDto.emailMedico());
            emailLog.setMailSubject(message.getSubject());
            emailLog.setMailText(message.getText());
            emailLog.setSendDateEmail(LocalDateTime.now());

            try {
                emailSender.send(message);
                emailLog.setStatus(EmailStatus.SENT);
            } catch (MailException e) {
                emailLog.setStatus(EmailStatus.ERROR);
                throw e;
            } finally {
                emailRepository.save(emailLog);
            }
        }
    }
}