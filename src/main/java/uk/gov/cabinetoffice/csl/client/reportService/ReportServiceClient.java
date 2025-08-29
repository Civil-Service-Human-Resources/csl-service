package uk.gov.cabinetoffice.csl.client.reportService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.client.model.DownloadableFile;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.AddReportRequestResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.GetReportRequestsResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.ReportType;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.Aggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionWithOrganisationAggregation;

import java.util.List;

@Slf4j
@Component
public class ReportServiceClient implements IReportServiceClient {

    private final IHttpClient httpClient;
    private final ReportServiceConfiguration config;

    public ReportServiceClient(@Qualifier("reportServiceHttpClient") IHttpClient httpClient,
                               ReportServiceConfiguration config) {
        this.httpClient = httpClient;
        this.config = config;
    }

    public AggregationResponse<Aggregation> getCourseCompletionAggregations(OrganisationIdsCourseCompletionsParams body) {
        log.debug("Getting course completion aggregation report with body '{}'", body);
        RequestEntity<OrganisationIdsCourseCompletionsParams> request = RequestEntity.post(config.getCourseCompletionsAggregationsUrl()).body(body);
        return httpClient.executeTypeReferenceRequest(request, new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public AggregationResponse<CourseCompletionAggregation> getCourseCompletionAggregationsByCourse(OrganisationIdsCourseCompletionsParams body) {
        log.debug("Getting course completion aggregation report by course with body '{}'", body);
        RequestEntity<OrganisationIdsCourseCompletionsParams> request = RequestEntity.post(config.getCourseCompletionsAggregationsByCourseUrl()).body(body);
        return httpClient.executeTypeReferenceRequest(request, new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public AggregationResponse<CourseCompletionWithOrganisationAggregation> getCourseCompletionAggregationsByCourseAndOrganisation(OrganisationIdsCourseCompletionsParams body) {
        log.debug("Getting course completion aggregation report by course and organisation with body '{}'", body);
        RequestEntity<OrganisationIdsCourseCompletionsParams> request = RequestEntity.post(config.getCourseCompletionsAggregationsByOrganisationUrl()).body(body);
        return httpClient.executeTypeReferenceRequest(request, new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public <T> AddReportRequestResponse postReportExportRequest(ReportType reportType, T body) {
        log.debug("Submitting course completion export request '{}'", body);
        RequestEntity<T> request = RequestEntity.post(config.getReportRequestUrl(reportType)).body(body);
        return httpClient.executeRequest(request, AddReportRequestResponse.class);
    }

    @Override
    public GetReportRequestsResponse getReportExportRequest(ReportType reportType, String userId, List<String> statuses) {
        String url = String.format("%s?userId=%s&status=%s", config.getReportRequestUrl(reportType), userId, String.join(",", statuses));
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeRequest(request, GetReportRequestsResponse.class);
    }

    @Override
    public DownloadableFile downloadCourseCompletionsReport(String slug) {
        String url = String.format("%s/downloads/%s", config.getRequestCourseCompletionReportUrl(), slug);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeFileDownloadRequest(request);
    }
}
