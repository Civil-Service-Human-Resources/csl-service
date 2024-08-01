package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.controller.model.GetCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationBinDelimiter;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseCompletionChart;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChartFactoryTest {

    @Mock
    private LearningCatalogueService learningCatalogueService;

    @InjectMocks
    private ChartFactory factory;

    @Test
    void testBuildMissingEntries() {
        ZoneId zoneId = ZoneId.of("+1");
        ZonedDateTime startDate = LocalDateTime.of(2024, 1, 1, 23, 0, 0)
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(zoneId);
        ZonedDateTime endDate = LocalDateTime.of(2024, 1, 2, 11, 0, 0)
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(zoneId);
        ;
        Map<String, Integer> data = Map.of(
                "2024-01-02T00:00:00", 1,
                "2024-01-02T05:00:00", 1,
                "2024-01-02T07:00:00", 1,
                "2024-01-02T09:00:00", 1
        );
        ChronoUnit unit = ChronoUnit.HOURS;
        LinkedHashMap<String, Integer> result = factory.buildMissingEntries(startDate, endDate, data, unit);
        assertEquals(13, result.size());
        assertEquals(1, result.get("2024-01-02T00:00:00"));
        assertEquals(0, result.get("2024-01-02T01:00:00"));
        assertEquals(0, result.get("2024-01-02T02:00:00"));
        assertEquals(0, result.get("2024-01-02T03:00:00"));
        assertEquals(0, result.get("2024-01-02T04:00:00"));
        assertEquals(1, result.get("2024-01-02T05:00:00"));
        assertEquals(0, result.get("2024-01-02T06:00:00"));
        assertEquals(1, result.get("2024-01-02T07:00:00"));
        assertEquals(0, result.get("2024-01-02T08:00:00"));
        assertEquals(1, result.get("2024-01-02T09:00:00"));
        assertEquals(0, result.get("2024-01-02T10:00:00"));
        assertEquals(0, result.get("2024-01-02T11:00:00"));
        assertEquals(0, result.get("2024-01-02T12:00:00"));
    }

    @Test
    void buildCourseCompletionsChart() {
        List<Course> courses = new ArrayList<>() {
            {
                Course course1 = new Course();
                course1.setId("course1");
                course1.setTitle("Course 1 title");
                add(course1);
                Course course2 = new Course();
                course2.setId("course2");
                course2.setTitle("Course 2 title");
                add(course2);
                Course course3 = new Course();
                course3.setId("course3");
                course3.setTitle("Course 3 title");
                add(course3);
                Course course4 = new Course();
                course4.setId("course4");
                course4.setTitle("Course 4 title");
                add(course4);
            }
        };
        LocalDateTime date1 = LocalDateTime.of(2024, 1, 1, 10, 0, 0, 0);
        LocalDateTime date2 = LocalDateTime.of(2024, 1, 1, 15, 0, 0, 0);
        List<CourseCompletionAggregation> aggregations = new ArrayList<>();
        aggregations.add(new CourseCompletionAggregation(date1, 10, "course1"));
        aggregations.add(new CourseCompletionAggregation(date1, 17, "course2"));
        aggregations.add(new CourseCompletionAggregation(date1, 42, "course3"));
        aggregations.add(new CourseCompletionAggregation(date2, 100, "course1"));
        aggregations.add(new CourseCompletionAggregation(date2, 21, "course2"));
        GetCourseCompletionsParams params = new GetCourseCompletionsParams();
        params.setStartDate(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        params.setEndDate(LocalDateTime.of(2024, 1, 2, 0, 0, 0));
        params.setTimezone(ZoneId.of("Europe/London"));
        params.setCourseIds(List.of("course1", "course2", "course3", "course4"));
        AggregationResponse<CourseCompletionAggregation> response = new AggregationResponse<>("Europe/London", AggregationBinDelimiter.HOUR, aggregations);

        when(learningCatalogueService.getCourses(List.of("course1", "course2", "course3", "course4"))).thenReturn(courses);

        CourseCompletionChart chart = factory.buildCourseCompletionsChart(params, response, true);

        assertEquals(190, chart.getTotal());
        assertEquals(69, chart.getChart().get("2024-01-01T10:00:00"));
        assertEquals(121, chart.getChart().get("2024-01-01T15:00:00"));
        assertEquals(110, chart.getCourseBreakdown().get("Course 1 title"));
        assertEquals(38, chart.getCourseBreakdown().get("Course 2 title"));
        assertEquals(42, chart.getCourseBreakdown().get("Course 3 title"));
        assertEquals(0, chart.getCourseBreakdown().get("Course 4 title"));
    }

}
