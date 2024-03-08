package uk.gov.cabinetoffice.csl.util;

import org.springframework.stereotype.Component;

import static java.util.UUID.randomUUID;

@Component
public class StringUtilService {

    public String generateRandomUuid() {
        return randomUUID().toString();
    }
}
