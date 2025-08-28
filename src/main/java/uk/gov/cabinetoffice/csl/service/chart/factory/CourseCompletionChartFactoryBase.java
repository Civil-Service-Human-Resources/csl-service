package uk.gov.cabinetoffice.csl.service.chart.factory;

import uk.gov.cabinetoffice.csl.client.reportService.IReportServiceClient;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.controller.model.SelectedOrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.identity.IdentityDto;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.IAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseCompletionChart;
import uk.gov.cabinetoffice.csl.service.chart.ChartWithBreakdowns;
import uk.gov.cabinetoffice.csl.service.chart.CourseCompletionChartType;
import uk.gov.cabinetoffice.csl.service.chart.builder.ChartBuilder;
import uk.gov.cabinetoffice.csl.service.csrs.OrganisationalUnitService;

import java.time.ZoneId;
import java.util.List;

public abstract class CourseCompletionChartFactoryBase<A extends IAggregation> {

    protected final OrganisationalUnitService organisationalUnitService;
    protected final IReportServiceClient reportServiceClient;
    protected final ChartBuilder<A> chartBuilder;

    protected CourseCompletionChartFactoryBase(OrganisationalUnitService organisationalUnitService,
                                               IReportServiceClient reportServiceClient, ChartBuilder<A> chartBuilder) {
        this.organisationalUnitService = organisationalUnitService;
        this.reportServiceClient = reportServiceClient;
        this.chartBuilder = chartBuilder;
    }

    public abstract CourseCompletionChartType getType();

    abstract AggregationResponse<A> getAggregations(OrganisationIdsCourseCompletionsParams params);

    protected OrganisationIdsCourseCompletionsParams getParamsFromApiParams(SelectedOrganisationIdsCourseCompletionsParams apiParams) {
        OrganisationIdsCourseCompletionsParams params = new OrganisationIdsCourseCompletionsParams();
        params.setStartDate(apiParams.getStartDate());
        params.setEndDate(apiParams.getEndDate());
        params.setTimezone(ZoneId.of(apiParams.getTimezone()));
        params.setCourseIds(apiParams.getCourseIds());
        params.setProfessionIds(apiParams.getProfessionIds());
        params.setGradeIds(apiParams.getGradeIds());
        if (apiParams.getSelectedOrganisationIds() != null) {
            params.setOrganisationIds(organisationalUnitService.getOrganisationsWithChildrenAsFlatList(apiParams.getSelectedOrganisationIds())
                    .stream().map(OrganisationalUnit::getId).toList());
        }
        return params;
    }

    protected ChartWithBreakdowns getAggregationsAndBuildCharts(OrganisationIdsCourseCompletionsParams params) {
        AggregationResponse<A> aggregations = getAggregations(params);
        return chartBuilder.buildCourseCompletionCharts(params, aggregations.getResults());
    }

    public CourseCompletionChart buildCourseCompletionsChart(SelectedOrganisationIdsCourseCompletionsParams apiParams, IdentityDto user) {
        OrganisationIdsCourseCompletionsParams params = getParamsFromApiParams(apiParams);
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
