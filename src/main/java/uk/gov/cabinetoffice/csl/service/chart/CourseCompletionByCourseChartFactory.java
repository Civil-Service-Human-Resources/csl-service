package uk.gov.cabinetoffice.csl.service.chart;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.reportService.IReportServiceClient;
import uk.gov.cabinetoffice.csl.controller.model.GetCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.identity.IdentityDto;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseCompletionChart;

import java.util.List;

@Slf4j
@Component
public class CourseCompletionByCourseChartFactory extends CourseCompletionChartFactoryBase {

    private final CourseCompletionsChartBuilder chartBuilder;

    public CourseCompletionByCourseChartFactory(IReportServiceClient reportServiceClient, CourseCompletionsChartBuilder chartBuilder) {
        super(reportServiceClient, chartBuilder);
        this.chartBuilder = chartBuilder;
    }

    @Override
    public CourseCompletionChartType getType() {
        return CourseCompletionChartType.BY_COURSE;
    }

    @Override
    public CourseCompletionChart buildCourseCompletionsChart(GetCourseCompletionsParams params, IdentityDto user) {
        AggregationResponse<CourseCompletionAggregation> aggregations = reportServiceClient.getCourseCompletionAggregationsByCourse(params);
        List<AggregationChart> charts = chartBuilder.buildCourseCompletionCharts(params, aggregations.getResults());
        boolean hasRequests = this.getHasRequests(user);
        return new CourseCompletionChart(charts.get(0), charts.get(1), params, hasRequests);
    }

}
