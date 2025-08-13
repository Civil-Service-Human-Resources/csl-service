package uk.gov.cabinetoffice.csl.service.chart.builder;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseBreakdown;
import uk.gov.cabinetoffice.csl.service.chart.AggregationChart;
import uk.gov.cabinetoffice.csl.service.chart.ChartWithBreakdowns;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CourseCompletionsChartBuilder<T extends CourseCompletionAggregation> extends ChartBuilder<T> {

    protected final LearningCatalogueService learningCatalogueService;

    public CourseCompletionsChartBuilder(LearningCatalogueService learningCatalogueService) {
        this.learningCatalogueService = learningCatalogueService;
    }

    public CourseBreakdown buildCourseBreakdown(String title, Collection<String> courseTitles) {
        return new CourseBreakdown(new LinkedHashMap<>(courseTitles.stream().collect(Collectors.toMap(o -> o, o -> 0))), title);
    }

    public ChartWithBreakdowns buildCourseCompletionCharts(OrganisationIdsCourseCompletionsParams params, List<T> aggregations) {
        AggregationChart chart = buildBasicChart(params.getStartDateZoned(),
                params.getEndDateZoned(), params.getBinDelimiterVal().getChronoUnit());
        Map<String, String> courseIdToTitleMap = learningCatalogueService.getCourseIdToTitleMap(params.getCourseIds());
        CourseBreakdown courseBreakdown = buildCourseBreakdown("Course breakdown", new ArrayList<>(courseIdToTitleMap.values()));
        for (CourseCompletionAggregation result : aggregations) {
            Integer total = result.getTotal();
            String courseId = result.getCourseId();
            String stringedDateTime = result.getDateBin().format(DateTimeFormatter.ISO_DATE_TIME);

            String courseTitle = courseIdToTitleMap.get(courseId);
            if (courseTitle != null) {
                courseBreakdown.putAndAggregate(courseTitle, total);
            }
            chart.putAndAggregate(stringedDateTime, total);
        }
        return new ChartWithBreakdowns(chart, courseBreakdown);
    }

}
