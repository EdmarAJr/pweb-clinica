package br.ifba.edu.clinica.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClinicaAMQPConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ClinicaAMQPConfiguration.class);

    @Bean
    public Queue criarFilaDeEnvioEmail(){
        return new Queue ("email.notificacao", false);
    }

    @Bean
    public Queue queueMedicoAtivo() {

        return new Queue("email.ativacao", false);
    }

    @Bean
    public RabbitAdmin criarRabbitAdmin(ConnectionFactory conn) {
        return new RabbitAdmin(conn);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConversor(){
        return  new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return  rabbitTemplate;
    }

     @Bean
     @ConditionalOnProperty(prefix = "clinica.rabbit", name = "enabled", havingValue = "true", matchIfMissing = true)
     public ApplicationListener<ApplicationReadyEvent> inicializaAdmin(RabbitAdmin rabbitAdmin){
         return event -> {
             try {
                 rabbitAdmin.initialize();
             } catch (Exception ex) {
                 logger.warn("Não foi possível inicializar RabbitAdmin - verifique se o RabbitMQ está rodando (localhost:5672). Exceção: {}", ex.toString());
             }
         };
     }
}