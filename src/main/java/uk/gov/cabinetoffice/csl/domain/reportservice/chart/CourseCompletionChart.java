package uk.gov.cabinetoffice.csl.domain.reportservice.chart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.service.chart.AggregationChart;

import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class CourseCompletionChart extends BasicChart {

    private Collection<CourseBreakdown> breakdowns;
    private boolean hasRequest;

    public CourseCompletionChart(AggregationChart chart, Collection<CourseBreakdown> breakdowns,
                                 OrganisationIdsCourseCompletionsParams params, boolean hasRequest) {
        super(chart.getRows(), params.getTimezone(), chart.getTotal(), params.getBinDelimiter().getVal());
        this.breakdowns = breakdowns;
        this.hasRequest = hasRequest;
    }

    public CourseCompletionChart(AggregationChart chart,
                                 OrganisationIdsCourseCompletionsParams params, boolean hasRequest) {
        super(chart.getRows(), params.getTimezone(), chart.getTotal(), params.getBinDelimiter().getVal());
        this.breakdowns = List.of();
        this.hasRequest = hasRequest;
    }
}
