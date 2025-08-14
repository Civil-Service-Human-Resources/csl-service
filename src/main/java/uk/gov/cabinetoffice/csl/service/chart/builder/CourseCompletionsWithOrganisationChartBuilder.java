package uk.gov.cabinetoffice.csl.service.chart.builder;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionWithOrganisationAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseBreakdown;
import uk.gov.cabinetoffice.csl.service.chart.AggregationChart;
import uk.gov.cabinetoffice.csl.service.chart.ChartWithBreakdowns;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class CourseCompletionsWithOrganisationChartBuilder extends CourseCompletionsChartBuilder<CourseCompletionWithOrganisationAggregation> {

    public CourseCompletionsWithOrganisationChartBuilder(LearningCatalogueService learningCatalogueService) {
        super(learningCatalogueService);
    }

    public ChartWithBreakdowns buildCourseCompletionCharts(Collection<CourseCompletionChartBuilderParams<CourseCompletionWithOrganisationAggregation>> params) {
        Map<String, String> courseIdToTitleMap = learningCatalogueService.getCourseIdToTitleMap(params.stream()
                .flatMap(tChartBuilderParams -> tChartBuilderParams.getParams().getCourseIds().stream()).toList());
        List<String> orgIdsCounted = new ArrayList<>();
        Collection<AggregationChart> charts = new ArrayList<>();
        Collection<CourseBreakdown> courseBreakdowns = new ArrayList<>();
        params.forEach(chartBuilderParams -> {
            AggregationChart chart = this.buildBasicChart(chartBuilderParams.getParams());
            CourseBreakdown courseBreakdown = buildCourseBreakdown(chartBuilderParams.getChartTitle(), new ArrayList<>(courseIdToTitleMap.values()));
            for (CourseCompletionWithOrganisationAggregation result : chartBuilderParams.getAggregations()) {
                Integer total = result.getTotal();
                String courseId = result.getCourseId();
                String stringedDateTime = result.getDateBin().format(DateTimeFormatter.ISO_DATE_TIME);

                String courseTitle = courseIdToTitleMap.get(courseId);
                if (courseTitle != null) {
                    courseBreakdown.putAndAggregate(courseTitle, total);
                }
                if (!orgIdsCounted.contains(result.getUniqueString())) {
                    chart.putAndAggregate(stringedDateTime, total);
                    orgIdsCounted.add(result.getUniqueString());
                }
            }
            courseBreakdowns.add(courseBreakdown);
            charts.add(chart);
        });
        AggregationChart totalChart = charts.stream().reduce(new AggregationChart(), AggregationChart::merge);
        return new ChartWithBreakdowns(totalChart, courseBreakdowns);
    }

}
