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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChartFactoryTest {

    @Mock
    private LearningCatalogueService learningCatalogueService;

    @InjectMocks
    private ChartFactory factory;

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
        LocalDateTime date2 = LocalDateTime.of(2024, 2, 1, 10, 0, 0, 0);
        List<CourseCompletionAggregation> aggregations = new ArrayList<>();
        aggregations.add(new CourseCompletionAggregation(date1, 10, "course1"));
        aggregations.add(new CourseCompletionAggregation(date1, 17, "course2"));
        aggregations.add(new CourseCompletionAggregation(date1, 42, "course3"));
        aggregations.add(new CourseCompletionAggregation(date2, 100, "course1"));
        aggregations.add(new CourseCompletionAggregation(date2, 21, "course2"));
        GetCourseCompletionsParams params = new GetCourseCompletionsParams();
        params.setStartDate(LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        params.setEndDate(LocalDateTime.of(2024, 1, 2, 10, 0, 0));
        params.setTimezone(ZoneId.of("Europe/London"));
        params.setCourseIds(List.of("course1", "course2", "course3", "course4"));
        AggregationResponse<CourseCompletionAggregation> response = new AggregationResponse<>("Europe/London", AggregationBinDelimiter.MONTH, aggregations);

        when(learningCatalogueService.getCourses(List.of("course1", "course2", "course3", "course4"))).thenReturn(courses);

        CourseCompletionChart chart = factory.buildCourseCompletionsChart(params, response, true);

        assertEquals(190, chart.getTotal());
        assertEquals("2024-01-01T10:00:00", chart.getChart().get(0).getX());
        assertEquals(69, chart.getChart().get(0).getY());
        assertEquals("2024-02-01T10:00:00", chart.getChart().get(1).getX());
        assertEquals(121, chart.getChart().get(1).getY());
        assertEquals(110, chart.getCourseBreakdown().get("Course 1 title"));
        assertEquals(38, chart.getCourseBreakdown().get("Course 2 title"));
        assertEquals(42, chart.getCourseBreakdown().get("Course 3 title"));
        assertEquals(0, chart.getCourseBreakdown().get("Course 4 title"));
    }

}
