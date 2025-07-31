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
import uk.gov.cabinetoffice.csl.controller.model.CreateReportRequestWithOrganisationIdsParams;
import uk.gov.cabinetoffice.csl.controller.model.CreateReportRequestWithSelectedOrganisationIdsParams;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.controller.model.SelectedOrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.error.ForbiddenException;
import uk.gov.cabinetoffice.csl.domain.error.NotFoundException;
import uk.gov.cabinetoffice.csl.domain.identity.IdentityDto;
import uk.gov.cabinetoffice.csl.domain.reportservice.AddCourseCompletionReportRequestResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseCompletionChart;
import uk.gov.cabinetoffice.csl.service.chart.ChartFactoryService;
import uk.gov.cabinetoffice.csl.service.chart.CourseCompletionChartFactoryBase;
import uk.gov.cabinetoffice.csl.service.chart.CourseCompletionChartType;
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
        CourseCompletionChartType type = isEmpty(params.getCourseIds()) ? CourseCompletionChartType.BASIC : CourseCompletionChartType.BY_COURSE;
        CourseCompletionChartFactoryBase factory = chartFactoryService.getFactory(type);

        OrganisationIdsCourseCompletionsParams organisationIdsCourseCompletionsParams = new OrganisationIdsCourseCompletionsParams();
        organisationIdsCourseCompletionsParams.setStartDate(params.getStartDate());
        organisationIdsCourseCompletionsParams.setEndDate(params.getEndDate());
        organisationIdsCourseCompletionsParams.setTimezone(ZoneId.of(params.getTimezone()));
        organisationIdsCourseCompletionsParams.setCourseIds(params.getCourseIds());
        organisationIdsCourseCompletionsParams.setProfessionIds(params.getProfessionIds());
        organisationIdsCourseCompletionsParams.setGradeIds(params.getGradeIds());
        if (params.getSelectedOrganisationIds() != null) {
            List<Long> organisationIds = organisationalUnitService.getOrganisationIdsWithChildrenAsFlatList(params.getSelectedOrganisationIds());
            organisationIdsCourseCompletionsParams.setOrganisationIds(organisationIds);
        }

        return factory.buildCourseCompletionsChart(organisationIdsCourseCompletionsParams, user);
    }

    @PreAuthorize("hasAnyAuthority('REPORT_EXPORT')")
    public AddCourseCompletionReportRequestResponse requestCourseCompletionsExport(CreateReportRequestWithSelectedOrganisationIdsParams params) {
        CreateReportRequestWithOrganisationIdsParams createReportServiceReportRequestParams = new CreateReportRequestWithOrganisationIdsParams();
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
            List<Long> organisationIds = organisationalUnitService.getOrganisationIdsWithChildrenAsFlatList(params.getSelectedOrganisationIds());
            createReportServiceReportRequestParams.setOrganisationIds(organisationIds);
        }

        return reportServiceClient.postCourseCompletionsExportRequest(createReportServiceReportRequestParams);
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

}
