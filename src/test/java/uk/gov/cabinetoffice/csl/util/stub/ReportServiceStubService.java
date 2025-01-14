package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Service
public class ReportServiceStubService {

    public void getCourseCompletionAggregations(String expectedInput, String response) {
        stubFor(
                WireMock.post(urlPathEqualTo("/report-service/course-completions/aggregations/by-course"))
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

    public void getReportRequests(String expectedUserId, List<String> expectedStatuses, String response) {
        stubFor(
                WireMock.get(urlPathEqualTo("/report-service/course-completions/report-requests"))
                        .withQueryParam("userId", equalTo(expectedUserId))
                        .withQueryParam("status", equalTo(String.join(",", expectedStatuses)))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public void postReportRequest(String expectedInput, String response) {
        stubFor(
                WireMock.post(urlPathEqualTo("/report-service/course-completions/report-requests"))
                        .withRequestBody(equalToJson(expectedInput, true, true))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

}
