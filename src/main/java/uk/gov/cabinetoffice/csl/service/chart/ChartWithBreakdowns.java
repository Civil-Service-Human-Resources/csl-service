package uk.gov.cabinetoffice.csl.service.chart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseBreakdown;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ChartWithBreakdowns {

    private final AggregationChart chart;
    private final Collection<CourseBreakdown> courseBreakdowns;

    public ChartWithBreakdowns(AggregationChart chart, CourseBreakdown courseBreakdown) {
        this.chart = chart;
        this.courseBreakdowns = List.of(courseBreakdown);
    }

    public ChartWithBreakdowns(AggregationChart chart) {
        this.chart = chart;
        this.courseBreakdowns = List.of();
    }
}
