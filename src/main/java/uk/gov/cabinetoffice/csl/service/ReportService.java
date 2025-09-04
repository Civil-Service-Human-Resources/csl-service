package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import uk.gov.cabinetoffice.csl.client.model.DownloadableFile;
import uk.gov.cabinetoffice.csl.client.reportService.IReportServiceClient;
import uk.gov.cabinetoffice.csl.controller.model.CourseCompletionReportRequestParams;
import uk.gov.cabinetoffice.csl.controller.model.CreateReportRequestWithSelectedOrganisationIdsParams;
import uk.gov.cabinetoffice.csl.controller.model.RegisteredLearnerReportRequestParams;
import uk.gov.cabinetoffice.csl.controller.model.SelectedOrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.error.ForbiddenException;
import uk.gov.cabinetoffice.csl.domain.error.NotFoundException;
import uk.gov.cabinetoffice.csl.domain.identity.IdentityDto;
import uk.gov.cabinetoffice.csl.domain.reportservice.AddReportRequestResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.GetReportRequestsResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.RegisteredLearnerOverview;
import uk.gov.cabinetoffice.csl.domain.reportservice.ReportType;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.IAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseCompletionChart;
import uk.gov.cabinetoffice.csl.service.chart.ChartFactoryService;
import uk.gov.cabinetoffice.csl.service.chart.CourseCompletionChartType;
import uk.gov.cabinetoffice.csl.service.chart.factory.CourseCompletionChartFactoryBase;
import uk.gov.cabinetoffice.csl.service.csrs.OrganisationalUnitListService;

import java.time.ZoneId;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

    private final IReportServiceClient reportServiceClient;
    private final ChartFactoryService chartFactoryService;
    private final OrganisationalUnitListService organisationalUnitService;

    public CourseCompletionChart getCourseCompletionsChart(SelectedOrganisationIdsCourseCompletionsParams params, IdentityDto user) {
        CourseCompletionChartType type;
        if (isEmpty(params.getCourseIds())) {
            type = CourseCompletionChartType.BASIC;
        } else {
            if (params.getSelectedOrganisationIds().size() <= 1) {
                type = CourseCompletionChartType.BY_COURSE;
            } else {
                type = CourseCompletionChartType.BY_ORGANISATION;
            }
        }
        CourseCompletionChartFactoryBase<? extends IAggregation> factory = chartFactoryService.getFactory(type);
        return factory.buildCourseCompletionsChart(params, user);
    }

    @PreAuthorize("hasAnyAuthority('REPORT_EXPORT')")
    public AddReportRequestResponse requestCourseCompletionsExport(CreateReportRequestWithSelectedOrganisationIdsParams params) {
        CourseCompletionReportRequestParams createReportServiceReportRequestParams = new CourseCompletionReportRequestParams();
        createReportServiceReportRequestParams.setStartDate(params.getStartDate());
        createReportServiceReportRequestParams.setEndDate(params.getEndDate());
        createReportServiceReportRequestParams.setTimezone(ZoneId.of(params.getTimezone()));
        createReportServiceReportRequestParams.setCourseIds(params.getCourseIds());
        createReportServiceReportRequestParams.setProfessionIds(params.getProfessionIds());
        createReportServiceReportRequestParams.setGradeIds(params.getGradeIds());
        createReportServiceReportRequestParams.setUserId(params.getUserId());
        createReportServiceReportRequestParams.setUserEmail(params.getUserEmail());
        createReportServiceReportRequestParams.setDownloadBaseUrl(params.getDownloadBaseUrl());
        createReportServiceReportRequestParams.setFullName(params.getFullName());
        if (params.getSelectedOrganisationIds() != null) {
            List<OrganisationalUnit> organisations = organisationalUnitService.getOrganisationsWithChildrenAsFlatList(params.getSelectedOrganisationIds());
            createReportServiceReportRequestParams.setOrganisationIds(organisations.stream().map(OrganisationalUnit::getId).toList());
        }

        return reportServiceClient.postReportExportRequest(ReportType.COURSE_COMPLETIONS, createReportServiceReportRequestParams);
    }

    @PreAuthorize("hasAnyAuthority('REPORT_EXPORT')")
    public AddReportRequestResponse requestRegisteredLearnerExport(RegisteredLearnerReportRequestParams params) {
        if (params.getOrganisationIds() != null) {
            List<OrganisationalUnit> organisations = organisationalUnitService.getOrganisationsWithChildrenAsFlatList(params.getOrganisationIds());
            params.setOrganisationIds(organisations.stream().map(OrganisationalUnit::getId).toList());
        }
        return reportServiceClient.postReportExportRequest(ReportType.REGISTERED_LEARNER, params);
    }

    @PreAuthorize("hasAnyAuthority('REPORT_EXPORT')")
    public DownloadableFile downloadCourseCompletionsReport(String slug) {
        try {
            return reportServiceClient.downloadCourseCompletionsReport(slug);
        } catch (RestClientResponseException e) {
            HttpStatusCode status = e.getStatusCode();
            if (status.equals(HttpStatus.FORBIDDEN)) {
                throw new ForbiddenException(e.getMessage());
            } else if (status.equals(HttpStatus.NOT_FOUND)) {
                throw new NotFoundException(e.getMessage());
            }
            throw e;
        }
    }

    public RegisteredLearnerOverview getRegisteredLearnerOverview(IdentityDto user) {
        GetReportRequestsResponse reportExportRequest = reportServiceClient.getReportExportRequest(ReportType.REGISTERED_LEARNER, user.getUid(), List.of("REQUESTED", "PROCESSING"));
        return new RegisteredLearnerOverview(reportExportRequest.hasRequests());
    }
}
