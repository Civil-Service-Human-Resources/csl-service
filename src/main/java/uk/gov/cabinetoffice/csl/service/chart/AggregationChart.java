package uk.gov.cabinetoffice.csl.service.chart;

import lombok.Getter;

import java.util.LinkedHashMap;

@Getter
public class AggregationChart extends LinkedHashMap<String, Integer> {

    private Integer total = 0;

    public void putAndAggregate(String key, Integer value) {
        merge(key, value, (existingValue, newValue) -> (existingValue != null ? existingValue : 0) + newValue);
        this.total = total + value;
    }

}
