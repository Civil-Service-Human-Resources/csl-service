package uk.gov.cabinetoffice.csl.domain.reportservice.chart;

import lombok.Getter;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.service.chart.AggregationChart;

import java.util.LinkedHashMap;

@Getter
@Setter
public class CourseBreakdown extends AggregationChart {

    private String title;

    public CourseBreakdown(LinkedHashMap<String, Integer> m, String title) {
        super(m);
        this.title = title;
    }

}
