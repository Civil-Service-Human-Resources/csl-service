package uk.gov.cabinetoffice.csl.service.chart;

import lombok.Getter;

import java.util.LinkedHashMap;

@Getter
public class AggregationChart {

    private Integer total;
    private LinkedHashMap<String, Integer> rows;

    public void putAndAggregate(String key, Integer value) {
        if (rows == null) {
            rows = new LinkedHashMap<>();
        }
        rows.merge(key, value, Integer::sum);
        this.total = total + value;
    }

    public AggregationChart merge(AggregationChart other) {
        other.getRows().forEach(this::putAndAggregate);
        return this;
    }

    public AggregationChart() {
        this.total = 0;
    }

    public AggregationChart(LinkedHashMap<String, Integer> m) {
        this(m, 0);
    }

    public AggregationChart(LinkedHashMap<String, Integer> m, Integer total) {
        this.rows = m;
        this.total = total;
    }

    public int get(String s) {
        return this.rows.get(s);
    }

    public int size() {
        return this.rows.size();
    }
}
