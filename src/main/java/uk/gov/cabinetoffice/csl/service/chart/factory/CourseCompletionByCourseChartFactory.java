package uk.gov.cabinetoffice.csl.service.chart.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.reportService.IReportServiceClient;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;
import uk.gov.cabinetoffice.csl.service.chart.ChartWithBreakdowns;
import uk.gov.cabinetoffice.csl.service.chart.CourseCompletionChartType;
import uk.gov.cabinetoffice.csl.service.chart.builder.CourseCompletionChartBuilderParams;
import uk.gov.cabinetoffice.csl.service.chart.builder.CourseCompletionsChartBuilder;
import uk.gov.cabinetoffice.csl.service.csrs.OrganisationalUnitListService;

@Slf4j
@Component
public class CourseCompletionByCourseChartFactory extends CourseCompletionChartFactoryBase<CourseCompletionAggregation> {

    protected final CourseCompletionsChartBuilder<CourseCompletionAggregation> chartBuilder;

    protected CourseCompletionByCourseChartFactory(OrganisationalUnitListService organisationalUnitListService,
                                                   IReportServiceClient reportServiceClient, CourseCompletionsChartBuilder<CourseCompletionAggregation> chartBuilder) {
        super(organisationalUnitListService, reportServiceClient, chartBuilder);
        this.chartBuilder = chartBuilder;
    }

    @Override
    public CourseCompletionChartType getType() {
        return CourseCompletionChartType.BY_COURSE;
    }

    @Override
    AggregationResponse<CourseCompletionAggregation> getAggregations(OrganisationIdsCourseCompletionsParams params) {
        return reportServiceClient.getCourseCompletionAggregationsByCourse(params);
    }

    protected CourseCompletionChartBuilderParams<CourseCompletionAggregation> getChartBuilderParams(OrganisationIdsCourseCompletionsParams params) {
        return new CourseCompletionChartBuilderParams<>(params, getAggregations(params).getResults(), "Course breakdown");
    }

    @Override
    protected ChartWithBreakdowns getAggregationsAndBuildCharts(OrganisationIdsCourseCompletionsParams params) {
        CourseCompletionChartBuilderParams<CourseCompletionAggregation> chartParams = getChartBuilderParams(params);
        return chartBuilder.buildCourseCompletionCharts(chartParams);
    }

}
