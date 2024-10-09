package uk.gov.cabinetoffice.csl.domain.reportservice;

import lombok.Getter;

import java.time.temporal.ChronoUnit;

@Getter
public enum AggregationBinDelimiter {

    HOUR("hour", ChronoUnit.HOURS),
    DAY("day", ChronoUnit.DAYS),
    WEEK("week", ChronoUnit.WEEKS),
    MONTH("month", ChronoUnit.MONTHS);

    private final String val;
    private final ChronoUnit chronoUnit;

    AggregationBinDelimiter(String val, ChronoUnit chronoUnit) {
        this.val = val;
        this.chronoUnit = chronoUnit;
    }

}
