package uk.gov.cabinetoffice.csl.config.messaging;

import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomServiceBusExceptionListener implements ExceptionListener {

    @Override
    public void onException(JMSException exception) {
        log.error("Custom JMSerror handler");
        log.error("Error code:" + exception.getErrorCode());
        log.error("Msg:" + exception.getMessage());
    }
}
