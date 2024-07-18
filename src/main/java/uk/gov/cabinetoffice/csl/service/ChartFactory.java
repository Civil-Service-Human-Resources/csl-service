package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.GetCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseCompletionChart;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.PlotPoint;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChartFactory {

    private final LearningCatalogueService learningCatalogueService;

    public CourseCompletionChart buildCourseCompletionsChart(GetCourseCompletionsParams params,
                                                             AggregationResponse<CourseCompletionAggregation> aggregations) {
        Map<String, String> courseIdToTitleMap = learningCatalogueService.getCourses(params.getCourseIds())
                .stream().collect(Collectors.toMap(Course::getId, Course::getTitle));
        Map<String, Integer> totalResults = new LinkedHashMap<>();
        Map<String, Integer> courseIdBreakdown = new HashMap<>();
        Integer courseSummaryTotal = 0;
        for (CourseCompletionAggregation result : aggregations.getResults()) {

            courseSummaryTotal += result.getTotal();
            String courseId = result.getCourseId();
            Integer courseTotal = courseIdBreakdown.get(courseId);
            if (courseTotal == null) {
                courseTotal = result.getTotal();
            } else {
                courseTotal = courseTotal + result.getTotal();
            }
            courseIdBreakdown.put(courseId, courseTotal);

            String stringedDateTime = result.getDateBin().format(DateTimeFormatter.ISO_DATE_TIME);
            Integer count = totalResults.get(stringedDateTime);
            if (count == null) {
                count = result.getTotal();
            } else {
                count = count + result.getTotal();
            }
            totalResults.put(stringedDateTime, count);
        }

        Map<String, Integer> courseTitleBreakdown = new HashMap<>();
        courseIdToTitleMap.forEach((id, title) -> {
            Integer countFromBreakdown = courseIdBreakdown.get(id);
            Integer count = countFromBreakdown == null ? 0 : countFromBreakdown;
            courseTitleBreakdown.put(title, count);
        });

        List<PlotPoint> plotPoints = totalResults.entrySet().stream()
                .map(e -> new PlotPoint(e.getKey(), e.getValue())).toList();
        return new CourseCompletionChart(plotPoints, courseTitleBreakdown, params.getTimezone(), courseSummaryTotal);
    }

}
