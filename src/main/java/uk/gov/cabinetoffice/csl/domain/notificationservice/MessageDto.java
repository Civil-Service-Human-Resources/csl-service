package uk.gov.cabinetoffice.csl.domain.notificationservice;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Data
@RequiredArgsConstructor
public class MessageDto {
    private final String reference;
    private final String recipient;
    private final Map<String, String> personalisation;
}
