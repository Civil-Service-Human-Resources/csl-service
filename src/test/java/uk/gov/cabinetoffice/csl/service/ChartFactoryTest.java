package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationBinDelimiter;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseCompletionChart;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChartFactoryTest {

    private final ChartFactory factory = new ChartFactory();

    @Test
    void buildCourseCompletionsChart() {
        ZoneId zone = ZoneId.of("UTC");
        ZonedDateTime date1 = ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, zone);
        ZonedDateTime date2 = ZonedDateTime.of(2024, 2, 1, 10, 0, 0, 0, zone);
        List<CourseCompletionAggregation> aggregations = new ArrayList<>();
        aggregations.add(new CourseCompletionAggregation(date1, 10, "course1"));
        aggregations.add(new CourseCompletionAggregation(date1, 17, "course2"));
        aggregations.add(new CourseCompletionAggregation(date1, 42, "course3"));
        aggregations.add(new CourseCompletionAggregation(date2, 100, "course1"));
        aggregations.add(new CourseCompletionAggregation(date2, 21, "course2"));
        AggregationResponse<CourseCompletionAggregation> response = new AggregationResponse(AggregationBinDelimiter.MONTH, aggregations);
        CourseCompletionChart chart = factory.buildCourseCompletionsChart(response);

        assertEquals(190, chart.getTotal());
        assertEquals("2024-01-01T10:00:00Z[UTC]", chart.getChart().get(0).getX());
        assertEquals(69, chart.getChart().get(0).getY());
        assertEquals("2024-02-01T10:00:00Z[UTC]", chart.getChart().get(1).getX());
        assertEquals(121, chart.getChart().get(1).getY());
        assertEquals(110, chart.getCourseBreakdown().get("course1"));
        assertEquals(110, chart.getCourseBreakdown().get("course1"));
        assertEquals(38, chart.getCourseBreakdown().get("course2"));
        assertEquals(42, chart.getCourseBreakdown().get("course3"));
    }
}
