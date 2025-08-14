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

    public ChartWithBreakdowns buildCourseCompletionCharts(CourseCompletionChartBuilderParams params, Map<String, String> courseIdToTitleMap) {
        return buildCourseCompletionCharts(params.getChartTitle(), params.getParams(), params.getAggregations(), courseIdToTitleMap);
    }

    public ChartWithBreakdowns buildCourseCompletionCharts(String title, OrganisationIdsCourseCompletionsParams params,
                                                           List<CourseCompletionAggregation> aggregations, Map<String, String> courseIdToTitleMap) {
        AggregationChart chart = buildBasicChart(params);
        CourseBreakdown courseBreakdown = buildCourseBreakdown(title, new ArrayList<>(courseIdToTitleMap.values()));
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

    public ChartWithBreakdowns buildCourseCompletionCharts(CourseCompletionChartBuilderParams params) {
        Map<String, String> courseIdToTitleMap = learningCatalogueService.getCourseIdToTitleMap(params.getParams().getCourseIds());
        return buildCourseCompletionCharts(params, courseIdToTitleMap);
    }

    public ChartWithBreakdowns buildCourseCompletionCharts(Collection<CourseCompletionChartBuilderParams> params) {
        Map<String, String> courseIdToTitleMap = learningCatalogueService.getCourseIdToTitleMap(params.stream()
                .flatMap(tChartBuilderParams -> tChartBuilderParams.getParams().getCourseIds().stream()).toList());
        ChartWithBreakdowns defaultChart = new ChartWithBreakdowns(new AggregationChart());
        return params.stream().map(chartBuilderParams -> buildCourseCompletionCharts(chartBuilderParams, courseIdToTitleMap.entrySet().stream()
                .filter(x -> chartBuilderParams.getParams().getCourseIds().contains(x.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
        ).reduce(defaultChart, (chartWithBreakdowns, chartWithBreakdowns2) -> chartWithBreakdowns2.merge(chartWithBreakdowns));
    }

}
