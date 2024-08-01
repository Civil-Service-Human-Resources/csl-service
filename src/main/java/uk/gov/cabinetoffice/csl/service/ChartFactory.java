package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.GetCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationBinDelimiter;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseCompletionChart;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChartFactory {

    private final LearningCatalogueService learningCatalogueService;

    public LinkedHashMap<String, Integer> buildMissingEntries(ZonedDateTime startDate, ZonedDateTime endDate,
                                                              Map<String, Integer> data, ChronoUnit interval) {
        LinkedHashMap<String, Integer> totalResults = new LinkedHashMap<>();
        LocalDateTime nextDate = startDate.withFixedOffsetZone().toLocalDateTime();
        nextDate = nextDate.with(LocalTime.MIDNIGHT);
        while (!nextDate.isAfter(endDate.toLocalDateTime())) {
            String label = nextDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Integer value = data.getOrDefault(label, 0);
            totalResults.put(label, value);
            nextDate = nextDate.plus(1, interval);
        }
        return totalResults;
    }

    public CourseCompletionChart buildCourseCompletionsChart(GetCourseCompletionsParams params,
                                                             AggregationResponse<CourseCompletionAggregation> aggregations,
                                                             boolean hasRequests) {
        AggregationBinDelimiter binDelimiter = params.getBinDelimiterVal();
        Map<String, String> courseIdToTitleMap = learningCatalogueService.getCourses(params.getCourseIds())
                .stream().collect(Collectors.toMap(Course::getId, Course::getTitle));

        Map<String, Integer> aggregationResults = new LinkedHashMap<>();
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
            Integer count = aggregationResults.get(stringedDateTime);
            if (count == null) {
                count = result.getTotal();
            } else {
                count = count + result.getTotal();
            }
            aggregationResults.put(stringedDateTime, count);
        }

        Map<String, Integer> totalResults = buildMissingEntries(params.getStartDateZoned(), params.getEndDateZoned(),
                aggregationResults, binDelimiter.getChronoUnit());

        Map<String, Integer> courseTitleBreakdown = new HashMap<>();
        courseIdToTitleMap.forEach((id, title) -> {
            Integer countFromBreakdown = courseIdBreakdown.get(id);
            Integer count = countFromBreakdown == null ? 0 : countFromBreakdown;
            courseTitleBreakdown.put(title, count);
        });

        return new CourseCompletionChart(totalResults, courseTitleBreakdown, params.getTimezone(), courseSummaryTotal,
                params.getBinDelimiter(), hasRequests);
    }

}
