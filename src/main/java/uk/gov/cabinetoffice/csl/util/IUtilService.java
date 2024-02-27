package uk.gov.cabinetoffice.csl.util;

import java.time.LocalDateTime;

public interface IUtilService {
    String generateUUID();

    LocalDateTime getNowDateTime();
}
