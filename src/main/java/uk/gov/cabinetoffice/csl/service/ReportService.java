package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.reportService.IReportServiceClient;
import uk.gov.cabinetoffice.csl.controller.model.CreateReportRequestParams;
import uk.gov.cabinetoffice.csl.controller.model.GetCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.AddCourseCompletionReportRequestResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.GetCourseCompletionReportRequestsResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseCompletionChart;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

    private final IReportServiceClient reportServiceClient;
    private final ChartFactory chartFactory;

    public CourseCompletionChart getCourseCompletionsChart(GetCourseCompletionsParams params, String userId) {
        AggregationResponse<CourseCompletionAggregation> results = reportServiceClient.getCourseCompletionAggregations(params);
        GetCourseCompletionReportRequestsResponse requestResponse = reportServiceClient.getCourseCompletionsExportRequest(userId,
                List.of("REQUESTED", "PROCESSING"));
        return chartFactory.buildCourseCompletionsChart(params, results, requestResponse.hasRequests());
    }

    public AddCourseCompletionReportRequestResponse requestCourseCompletionsExport(CreateReportRequestParams params) {
        return reportServiceClient.postCourseCompletionsExportRequest(params);
    }
}
