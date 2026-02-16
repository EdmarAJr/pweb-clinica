package br.ifba.edu.email_service.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import br.ifba.edu.email_service.dtos.DadosConsultaDTO;
import br.ifba.edu.email_service.dtos.DadosNotificacaoMedicoDTO;
import br.ifba.edu.email_service.service.EmailService;

@Component
public class ConsultaListener {

    private final EmailService service;
    private final Logger logger = LoggerFactory.getLogger(ConsultaListener.class);

    public ConsultaListener(EmailService service) {
        this.service = service;
    }

    @RabbitListener(queues = "email.notificacao")
    public void recebeMensagem(@Payload DadosConsultaDTO consulta) {
        System.out.println(consulta);
        service.sendEmail(consulta);
        logger.info("Recebi a mensagem de consulta: {}", consulta);
    }

    @RabbitListener(queues = "email.ativacao")
    public void recebeNotificacaoMedico(@Payload DadosNotificacaoMedicoDTO medicoDto) {
        System.out.println(medicoDto);
        service.sendEmail(medicoDto);
        logger.info("Recebi a notificação ativação de médico: {}", medicoDto);
    }
}