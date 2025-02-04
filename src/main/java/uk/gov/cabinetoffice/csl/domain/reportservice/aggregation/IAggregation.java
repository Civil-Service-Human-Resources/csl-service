package uk.gov.cabinetoffice.csl.domain.reportservice.aggregation;

import java.time.LocalDateTime;

public interface IAggregation {
    LocalDateTime getDateBin();

    Integer getTotal();
}
