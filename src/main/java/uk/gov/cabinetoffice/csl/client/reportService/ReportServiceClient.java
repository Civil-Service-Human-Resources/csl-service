package uk.gov.cabinetoffice.csl.client.reportService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.client.model.DownloadableFile;
import uk.gov.cabinetoffice.csl.controller.model.CreateReportRequestParams;
import uk.gov.cabinetoffice.csl.controller.model.GetCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.AddCourseCompletionReportRequestResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.GetCourseCompletionReportRequestsResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.Aggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;

import java.util.List;

@Slf4j
@Component
public class ReportServiceClient implements IReportServiceClient {

    @Value("${reportService.courseCompletionsAggregationsByCourseUrl}")
    private String courseCompletionAggregationsByCourse;

    @Value("${reportService.courseCompletionsAggregationsUrl}")
    private String courseCompletionAggregations;

    @Value("${reportService.requestCourseCompletionReportUrl}")
    private String requestCourseCompletionReport;

    private final IHttpClient httpClient;

    public ReportServiceClient(@Qualifier("reportServiceHttpClient") IHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public AggregationResponse<Aggregation> getCourseCompletionAggregations(GetCourseCompletionsParams body) {
        log.debug("Getting course completion aggregation report with body '{}'", body);
        RequestEntity<GetCourseCompletionsParams> request = RequestEntity.post(courseCompletionAggregations).body(body);
        return httpClient.executeTypeReferenceRequest(request);
    }

    @Override
    public AggregationResponse<CourseCompletionAggregation> getCourseCompletionAggregationsByCourse(GetCourseCompletionsParams body) {
        log.debug("Getting course completion aggregation report by course with body '{}'", body);
        RequestEntity<GetCourseCompletionsParams> request = RequestEntity.post(courseCompletionAggregationsByCourse).body(body);
        return httpClient.executeTypeReferenceRequest(request);
    }

    @Override
    public AddCourseCompletionReportRequestResponse postCourseCompletionsExportRequest(CreateReportRequestParams body) {
        log.debug("Submitting course completion export request '{}'", body);
        RequestEntity<CreateReportRequestParams> request = RequestEntity.post(requestCourseCompletionReport).body(body);
        return httpClient.executeRequest(request, AddCourseCompletionReportRequestResponse.class);
    }

    @Override
    public GetCourseCompletionReportRequestsResponse getCourseCompletionsExportRequest(String userId, List<String> statuses) {
        String url = String.format("%s?userId=%s&status=%s", requestCourseCompletionReport, userId, String.join(",", statuses));
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeRequest(request, GetCourseCompletionReportRequestsResponse.class);
    }

    @Override
    public DownloadableFile downloadCourseCompletionsReport(String slug) {
        String url = String.format("%s/downloads/%s", requestCourseCompletionReport, slug);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeFileDownloadRequest(request);
    }
}
