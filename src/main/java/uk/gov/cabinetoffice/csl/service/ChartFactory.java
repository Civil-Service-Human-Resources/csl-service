package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.Aggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseCompletionChart;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.PlotPoint;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ChartFactory {

    public CourseCompletionChart buildCourseCompletionsChart(AggregationResponse<CourseCompletionAggregation> aggregations) {
        Map<String, Integer> totalResults = new LinkedHashMap<>();
        for (Aggregation result : aggregations.getResults()) {
            String stringedDateTime = result.getDateBin().format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
            Integer count = totalResults.get(stringedDateTime);
            if (count == null) {
                count = result.getTotal();
            } else {
                count = count + result.getTotal();
            }
            totalResults.put(stringedDateTime, count);
        }
        List<PlotPoint> plotPoints = totalResults.entrySet().stream()
                .map(e -> new PlotPoint(e.getKey(), e.getValue())).toList();
        return new CourseCompletionChart(plotPoints);
    }

}
