package uk.gov.cabinetoffice.csl.domain.reportservice.chart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.service.chart.AggregationChart;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class CourseCompletionChart extends BasicChart {

    private Map<String, Integer> courseBreakdown;
    private boolean hasRequest;
    
    public CourseCompletionChart(AggregationChart chart, AggregationChart courseBreakdown,
                                 OrganisationIdsCourseCompletionsParams params, boolean hasRequest) {
        super(chart, params.getTimezone(), chart.getTotal(), params.getBinDelimiter().getVal());
        this.courseBreakdown = courseBreakdown;
        this.hasRequest = hasRequest;
    }
}
