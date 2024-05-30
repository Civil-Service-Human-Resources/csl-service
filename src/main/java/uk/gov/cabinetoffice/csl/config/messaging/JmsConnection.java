package uk.gov.cabinetoffice.csl.config.messaging;

import jakarta.jms.ConnectionFactory;

public interface JmsConnection {

    ConnectionFactory buildConnectionFactory();
    
}
