package uk.gov.cabinetoffice.csl.service.chart;

import uk.gov.cabinetoffice.csl.client.reportService.IReportServiceClient;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.identity.IdentityDto;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseCompletionChart;
import uk.gov.cabinetoffice.csl.service.csrs.CivilServantRegistryService;

import java.util.List;

public abstract class CourseCompletionChartFactoryBase {

    protected final IReportServiceClient reportServiceClient;
    protected final ChartBuilder chartBuilder;
    protected final CivilServantRegistryService civilServantRegistryService;

    protected CourseCompletionChartFactoryBase(IReportServiceClient reportServiceClient, ChartBuilder chartBuilder, CivilServantRegistryService civilServantRegistryService) {
        this.reportServiceClient = reportServiceClient;
        this.chartBuilder = chartBuilder;
        this.civilServantRegistryService = civilServantRegistryService;
    }

    abstract CourseCompletionChartType getType();

    public abstract CourseCompletionChart buildCourseCompletionsChart(OrganisationIdsCourseCompletionsParams params, IdentityDto user);

    protected boolean getHasRequests(IdentityDto user) {
        boolean hasRequests = false;
        if (user.hasRole("REPORT_EXPORT")) {
            hasRequests = reportServiceClient.getCourseCompletionsExportRequest(user.getUid(), List.of("REQUESTED", "PROCESSING")).hasRequests();
        }
        return hasRequests;
    }

}
