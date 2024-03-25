package uk.gov.cabinetoffice.csl.configuration;

import jakarta.jms.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.Mockito.mock;


public class MockJmsConfig {

    @Bean
    @Primary
    public JmsTemplate fakeJmsTemplate() {
        return mock(JmsTemplate.class);
    }

    @Bean
    @Primary
    public ConnectionFactory fakeConnectionFactory() {
        return mock(ConnectionFactory.class);
    }

}
