package uk.gov.cabinetoffice.csl.service.chart.builder;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionWithOrganisationAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseBreakdown;
import uk.gov.cabinetoffice.csl.service.chart.AggregationChart;
import uk.gov.cabinetoffice.csl.service.chart.ChartWithBreakdowns;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CourseCompletionsWithOrganisationsChartBuilder extends CourseCompletionsChartBuilder<CourseCompletionWithOrganisationAggregation> {
    public CourseCompletionsWithOrganisationsChartBuilder(LearningCatalogueService learningCatalogueService) {
        super(learningCatalogueService);
    }

    @Override
    public ChartWithBreakdowns buildCourseCompletionCharts(OrganisationIdsCourseCompletionsParams params, List<CourseCompletionWithOrganisationAggregation> aggregations) {
        AggregationChart chart = buildBasicChart(params.getStartDateZoned(),
                params.getEndDateZoned(), params.getBinDelimiterVal().getChronoUnit());
        Map<String, String> courseIdToTitleMap = learningCatalogueService.getCourseIdToTitleMap(params.getCourseIds());
        Map<Long, CourseBreakdown> orgBreakdowns = new HashMap<>();
        params.getOrgMap().forEach((key, value) -> orgBreakdowns.put(key, buildCourseBreakdown(value, courseIdToTitleMap.values())));
        for (CourseCompletionWithOrganisationAggregation result : aggregations) {
            Integer total = result.getTotal();
            String courseId = result.getCourseId();
            String stringedDateTime = result.getDateBin().format(DateTimeFormatter.ISO_DATE_TIME);

            String courseTitle = courseIdToTitleMap.get(courseId);
            if (courseTitle != null) {
                CourseBreakdown courseBreakdown = orgBreakdowns.get(result.getOrganisationId());
                if (courseBreakdown != null) {
                    courseBreakdown.putAndAggregate(courseTitle, total);
                }
            }
            chart.putAndAggregate(stringedDateTime, total);
        }
        return new ChartWithBreakdowns(chart, orgBreakdowns.values());
    }
}
