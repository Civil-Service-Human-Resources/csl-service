package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Service
public class ReportServiceStubService {

    public void getCourseCompletionAggregations(String expectedInput, String response) {
        stubFor(
                WireMock.post(urlPathEqualTo("/report-service/course-completions/aggregations"))
                        .withRequestBody(equalToJson(expectedInput, true, true))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public void getCourseCompletionAggregationsByCourse(String expectedInput, String response) {
        stubFor(
                WireMock.post(urlPathEqualTo("/report-service/course-completions/aggregations/by-course"))
                        .withRequestBody(equalToJson(expectedInput, true, true))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public void getCourseCompletionAggregationsByCourseAndOrganisation(String expectedInput, String response) {
        stubFor(
                WireMock.post(urlPathEqualTo("/report-service/course-completions/aggregations/by-organisation"))
                        .withRequestBody(equalToJson(expectedInput, true, true))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public void downloadCourseCompletionReport(String slug, String fileName, String content) throws IOException {
        ByteArrayOutputStream resp = new ByteArrayOutputStream();
        resp.write(content.getBytes(StandardCharsets.UTF_8));
        stubFor(
                WireMock.get(String.format("/report-service/course-completions/report-requests/downloads/%s", slug))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", fileName))
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                                .withBody(resp.toByteArray())
                        ));
    }

    public void downloadCourseCompletionReportNotFound(String slug) {
        stubFor(
                WireMock.get(String.format("/report-service/course-completions/report-requests/downloads/%s", slug))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(status(404)));
    }

    public void postReportRequest(String reportUrlPart, String expectedInput, String response) {
        stubFor(
                WireMock.post(urlPathEqualTo(String.format("/report-service/%s/report-requests", reportUrlPart)))
                        .withRequestBody(equalToJson(expectedInput, true, true))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public StubMapping getReportRequests(String userId, String statuses, String response) {
        MappingBuilder mappingBuilder = WireMock.get(urlPathEqualTo("/report-service/registered-learners/report-requests"))
                .withQueryParam("userId", equalTo(userId))
                .withQueryParam("status", equalTo(statuses));
        return stubFor(
                mappingBuilder
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }
}
