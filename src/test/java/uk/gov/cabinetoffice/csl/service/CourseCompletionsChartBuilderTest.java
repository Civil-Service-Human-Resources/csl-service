package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseBreakdown;
import uk.gov.cabinetoffice.csl.service.chart.AggregationChart;
import uk.gov.cabinetoffice.csl.service.chart.ChartWithBreakdowns;
import uk.gov.cabinetoffice.csl.service.chart.builder.CourseCompletionChartBuilderParams;
import uk.gov.cabinetoffice.csl.service.chart.builder.CourseCompletionsChartBuilder;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseCompletionsChartBuilderTest {

    @Mock
    private LearningCatalogueService learningCatalogueService;

    @InjectMocks
    CourseCompletionsChartBuilder<CourseCompletionAggregation> chartBuilder;

    @Test
    void buildCourseCompletionsChart() {
        Map<String, String> courseMap = Map.of(
                "course1", "Course 1 title",
                "course2", "Course 2 title",
                "course3", "Course 3 title",
                "course4", "Course 4 title");

        LocalDateTime date1 = LocalDateTime.of(2024, 1, 1, 10, 0, 0, 0);
        LocalDateTime date2 = LocalDateTime.of(2024, 1, 1, 15, 0, 0, 0);
        List<CourseCompletionAggregation> aggregations = new ArrayList<>();
        aggregations.add(new CourseCompletionAggregation(date1, 10, "course1"));
        aggregations.add(new CourseCompletionAggregation(date1, 17, "course2"));
        aggregations.add(new CourseCompletionAggregation(date1, 42, "course3"));
        aggregations.add(new CourseCompletionAggregation(date2, 100, "course1"));
        aggregations.add(new CourseCompletionAggregation(date2, 21, "course2"));

        OrganisationIdsCourseCompletionsParams params = new OrganisationIdsCourseCompletionsParams();
        params.setStartDate(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        params.setEndDate(LocalDateTime.of(2024, 1, 2, 0, 0, 0));
        params.setTimezone(ZoneId.of("Europe/London"));
        params.setCourseIds(List.of("course1", "course2", "course3", "course4"));

        when(learningCatalogueService.getCourseIdToTitleMap(List.of("course1", "course2", "course3", "course4"))).thenReturn(courseMap);

        CourseCompletionChartBuilderParams chartParams = new CourseCompletionChartBuilderParams(params, aggregations, "Test tile");

        ChartWithBreakdowns charts = chartBuilder.buildCourseCompletionCharts(chartParams);
        AggregationChart aggregationChart = charts.getChart();
        List<CourseBreakdown> courseBreakdowns = new ArrayList<>(charts.getCourseBreakdowns());

        assertEquals(190, aggregationChart.getTotal());
        assertEquals(69, aggregationChart.get("2024-01-01T10:00:00"));
        assertEquals(121, aggregationChart.get("2024-01-01T15:00:00"));

        assertEquals(110, courseBreakdowns.get(0).get("Course 1 title"));
        assertEquals(38, courseBreakdowns.get(0).get("Course 2 title"));
        assertEquals(42, courseBreakdowns.get(0).get("Course 3 title"));
        assertEquals(0, courseBreakdowns.get(0).get("Course 4 title"));
    }
}
