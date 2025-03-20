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
import uk.gov.cabinetoffice.csl.controller.model.CreateReportRequestParams;
import uk.gov.cabinetoffice.csl.controller.model.GetCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.error.ForbiddenException;
import uk.gov.cabinetoffice.csl.domain.error.NotFoundException;
import uk.gov.cabinetoffice.csl.domain.identity.IdentityDto;
import uk.gov.cabinetoffice.csl.domain.reportservice.AddCourseCompletionReportRequestResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseCompletionChart;
import uk.gov.cabinetoffice.csl.service.chart.ChartFactoryService;
import uk.gov.cabinetoffice.csl.service.chart.CourseCompletionChartFactoryBase;
import uk.gov.cabinetoffice.csl.service.chart.CourseCompletionChartType;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

    private final IReportServiceClient reportServiceClient;
    private final ChartFactoryService chartFactoryService;

    public CourseCompletionChart getCourseCompletionsChart(GetCourseCompletionsParams params, IdentityDto user) {
        CourseCompletionChartType type = isEmpty(params.getCourseIds()) ? CourseCompletionChartType.BASIC : CourseCompletionChartType.BY_COURSE;
        CourseCompletionChartFactoryBase factory = chartFactoryService.getFactory(type);
        return factory.buildCourseCompletionsChart(params, user);
    }

    @PreAuthorize("hasAnyAuthority('REPORT_EXPORT')")
    public AddCourseCompletionReportRequestResponse requestCourseCompletionsExport(CreateReportRequestParams params) {
        return reportServiceClient.postCourseCompletionsExportRequest(params);
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
