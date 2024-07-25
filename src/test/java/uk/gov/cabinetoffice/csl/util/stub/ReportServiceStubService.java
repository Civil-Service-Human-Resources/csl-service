package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Service
public class ReportServiceStubService {

    public void getCourseCompletionAggregations(String expectedInput, String response) {
        stubFor(
                WireMock.post(urlPathEqualTo("/report-service/course-completions/aggregations/by-course"))
                        .withRequestBody(equalToJson(expectedInput, true, true))
                        .withHeader("Authorization", equalTo("Bearer fakeToken"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public void getReportRequests(String expectedUserId, List<String> expectedStatuses, String response) {
        stubFor(
                WireMock.get(urlPathEqualTo("/report-service/course-completions/report-requests"))
                        .withQueryParam("userId", equalTo(expectedUserId))
                        .withQueryParam("status", equalTo(String.join(",", expectedStatuses)))
                        .withHeader("Authorization", equalTo("Bearer fakeToken"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

}
