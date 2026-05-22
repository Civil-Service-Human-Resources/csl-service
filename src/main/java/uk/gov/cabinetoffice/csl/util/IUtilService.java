package uk.gov.cabinetoffice.csl.util;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.List;

public interface IUtilService {
    String generateUUID();

    LocalDateTime getNowDateTime();

    Long getDurationUntilTomorrow(TemporalUnit unit);

    <T> List<List<T>> batchList(List<T> list, Integer batchSize);
}
