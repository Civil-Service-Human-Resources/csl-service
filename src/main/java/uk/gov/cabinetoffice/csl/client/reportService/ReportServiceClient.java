package uk.gov.cabinetoffice.csl.client.reportService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.controller.model.GetCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseCompletionAggregation;

import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class ReportServiceClient implements IReportServiceClient {

    @Value("${reportService.courseCompletionsAggregationsUrl}")
    private String courseCompletionAggregationsByCourse;

    private final IHttpClient httpClient;

    public ReportServiceClient(@Qualifier("reportServiceHttpClient") IHttpClient httpClient) {
        this.httpClient = httpClient;
    }


    @Override
    public AggregationResponse<CourseCompletionAggregation> getCourseCompletionAggregations(GetCourseCompletionsParams params) {
        String url = String.format("%s?startDate=%s&endDate=%s&organisationIds=%s&courseIds=%s&professionIds=%s&gradeIds=%s&binDelimiter=%s",
                courseCompletionAggregationsByCourse, params.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE), params.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                params.getOrganisationIdsAsString(), params.getCourseIdsAsString(), params.getProfessionIdsAsString(), params.getGradeIdsAsString(), params.getBinDelimiter().name());
        log.debug("Getting course completion aggregation report with params '{}'", params);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeTypeReferenceRequest(request, new ParameterizedTypeReference<>() {
        });
    }
}
