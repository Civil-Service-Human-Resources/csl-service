package uk.gov.cabinetoffice.csl.util;

import java.time.LocalDateTime;
import java.util.List;

public interface IUtilService {
    String generateUUID();

    LocalDateTime getNowDateTime();

    <T> List<List<T>> batchList(List<T> list, Integer batchSize);
}
