package uk.gov.cabinetoffice.csl.service.chart;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.controller.model.GetCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CourseCompletionsChartBuilder extends ChartBuilder {

    private final LearningCatalogueService learningCatalogueService;

    public CourseCompletionsChartBuilder(LearningCatalogueService learningCatalogueService) {
        this.learningCatalogueService = learningCatalogueService;
    }

    public List<AggregationChart> buildCourseCompletionCharts(GetCourseCompletionsParams params, List<CourseCompletionAggregation> aggregations) {
        AggregationChart chart = buildBasicChart(params.getStartDateZoned(),
                params.getEndDateZoned(), params.getBinDelimiterVal().getChronoUnit());
        Map<String, String> courseIdToTitleMap = learningCatalogueService.getCourseIdToTitleMap(params.getCourseIds());
        AggregationChart courseBreakdown = buildBasicChart(new ArrayList<>(courseIdToTitleMap.values()));
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
        return List.of(chart, courseBreakdown);
    }

}
