package uk.gov.cabinetoffice.csl.service.chart.factory;

import uk.gov.cabinetoffice.csl.client.reportService.IReportServiceClient;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.identity.IdentityDto;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.IAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseCompletionChart;
import uk.gov.cabinetoffice.csl.service.chart.ChartWithBreakdowns;
import uk.gov.cabinetoffice.csl.service.chart.CourseCompletionChartType;
import uk.gov.cabinetoffice.csl.service.chart.builder.ChartBuilder;

import java.util.List;

public abstract class CourseCompletionChartFactoryBase<A extends IAggregation> {

    protected final IReportServiceClient reportServiceClient;
    protected final ChartBuilder<A> chartBuilder;

    protected CourseCompletionChartFactoryBase(IReportServiceClient reportServiceClient, ChartBuilder<A> chartBuilder) {
        this.reportServiceClient = reportServiceClient;
        this.chartBuilder = chartBuilder;
    }

    public abstract CourseCompletionChartType getType();

    abstract AggregationResponse<A> getAggregations(OrganisationIdsCourseCompletionsParams params);

    protected ChartWithBreakdowns getAggregationsAndBuildCharts(OrganisationIdsCourseCompletionsParams params) {
        AggregationResponse<A> aggregations = getAggregations(params);
        return chartBuilder.buildCourseCompletionCharts(params, aggregations.getResults());
    }

    public CourseCompletionChart buildCourseCompletionsChart(OrganisationIdsCourseCompletionsParams params, IdentityDto user) {
        ChartWithBreakdowns charts = getAggregationsAndBuildCharts(params);
        boolean hasRequests = this.getHasRequests(user);
        return new CourseCompletionChart(charts.getChart(), charts.getCourseBreakdowns(), params, hasRequests);
    }

    protected boolean getHasRequests(IdentityDto user) {
        boolean hasRequests = false;
        if (user.hasRole("REPORT_EXPORT")) {
            hasRequests = reportServiceClient.getCourseCompletionsExportRequest(user.getUid(), List.of("REQUESTED", "PROCESSING")).hasRequests();
        }
        return hasRequests;
    }

}
