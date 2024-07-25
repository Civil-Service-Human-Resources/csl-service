package uk.gov.cabinetoffice.csl.domain.reportservice.chart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class CourseCompletionChart extends BasicChart {

    private Map<String, Integer> courseBreakdown;
    private boolean hasRequest;

    public CourseCompletionChart(List<PlotPoint> chart, Map<String, Integer> courseBreakdown,
                                 String timezone, Integer total, String delimiter, boolean hasRequest) {
        super(chart, timezone, total, delimiter);
        this.courseBreakdown = courseBreakdown;
        this.hasRequest = hasRequest;
    }
}
