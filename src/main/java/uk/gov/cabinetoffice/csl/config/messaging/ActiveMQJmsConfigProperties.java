package uk.gov.cabinetoffice.csl.config.messaging;

import jakarta.jms.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "messaging.activemq")
@RequiredArgsConstructor
public class ActiveMQJmsConfigProperties implements JmsConnection {

    private final String url;
    private final String username;
    private final String password;

    @Override
    public ConnectionFactory buildConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(url);
        connectionFactory.setUserName(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

}
