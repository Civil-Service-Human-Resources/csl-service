package uk.gov.cabinetoffice.csl.service.messaging.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.util.IUtilService;

@Service
@RequiredArgsConstructor
public class MessageFactory {

    private final IUtilService utilService;

    public Message<IMessageMetadata> generateFullMessage(IMessageMetadata metadata) {
        return new Message<>(utilService.generateUUID(), utilService.getNowDateTime(), metadata);
    }
}
