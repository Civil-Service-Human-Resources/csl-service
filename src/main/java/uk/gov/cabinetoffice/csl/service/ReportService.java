package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import uk.gov.cabinetoffice.csl.client.model.DownloadableFile;
import uk.gov.cabinetoffice.csl.client.reportService.IReportServiceClient;
import uk.gov.cabinetoffice.csl.controller.model.CreateReportRequestParams;
import uk.gov.cabinetoffice.csl.controller.model.GetCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.error.ForbiddenException;
import uk.gov.cabinetoffice.csl.domain.identity.IdentityDto;
import uk.gov.cabinetoffice.csl.domain.reportservice.AddCourseCompletionReportRequestResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseCompletionChart;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

    private final IReportServiceClient reportServiceClient;
    private final ChartFactory chartFactory;

    public CourseCompletionChart getCourseCompletionsChart(GetCourseCompletionsParams params, IdentityDto user) {
        AggregationResponse<CourseCompletionAggregation> results = reportServiceClient.getCourseCompletionAggregations(params);
        boolean hasRequests = false;
        if (user.hasRole("REPORT_EXPORT")) {
            hasRequests = reportServiceClient.getCourseCompletionsExportRequest(user.getUid(), List.of("REQUESTED", "PROCESSING")).hasRequests();
        }
        return chartFactory.buildCourseCompletionsChart(params, results, hasRequests);
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
            if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
                throw new ForbiddenException(e.getMessage());
            }
            throw e;
        }
    }
}
