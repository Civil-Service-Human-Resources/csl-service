package uk.gov.cabinetoffice.csl.config.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Slf4j
@Configuration
public class JmsConfig {


    @Bean
    @ConditionalOnProperty(value = "spring.jms.servicebus.enabled", havingValue = "false")
    public ConnectionFactory getActiveMQConnectionFactory(ActiveMQJmsConfigProperties properties) {
        log.info("Service bus is disabled; defaulting to activeMQ/Artemis");
        return properties.buildConnectionFactory();
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper mapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setObjectMapper(mapper);
        return converter;
    }

    @Bean
    public JmsTemplate customJmsTemplate(ConnectionFactory connectionFactory, MessageConverter jacksonJmsMessageConverter) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setMessageConverter(jacksonJmsMessageConverter);
        return jmsTemplate;
    }
}
