package uk.gov.cabinetoffice.csl.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import uk.gov.cabinetoffice.csl.configuration.TestConfig;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.CSLServiceWireMockServer;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles({"wiremock", "no-redis"})
@Import(TestConfig.class)
public class ReportTest extends CSLServiceWireMockServer {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CSLStubService cslStubService;

    @Test
    public void testGetAggregations() {
        String response = """
                {
                  "timezone": "+01:00",
                  "delimiter": "hour",
                  "results": [
                    {
                      "courseId": "course1",
                      "total": 10,
                      "dateBin": "2024-01-01T10:00:00"
                    },
                    {
                      "courseId": "course2",
                      "total": 14,
                      "dateBin": "2024-01-01T10:00:00"
                    },
                    {
                      "courseId": "course1",
                      "total": 50,
                      "dateBin": "2024-01-01T11:00:00"
                    },
                    {
                      "courseId": "course2",
                      "total": 20,
                      "dateBin": "2024-01-01T11:00:00"
                    },
                    {
                      "courseId": "course1",
                      "total": 13,
                      "dateBin": "2024-01-01T12:00:00"
                    },
                    {
                      "courseId": "course2",
                      "total": 90,
                      "dateBin": "2024-01-01T12:00:00"
                    },
                    {
                      "courseId": "course1",
                      "total": 12,
                      "dateBin": "2024-01-01T13:00:00"
                    },
                    {
                      "courseId": "course2",
                      "total": 9,
                      "dateBin": "2024-01-01T13:00:00"
                    }
                  ]
                }
                """;
        String expectedInput = """
                {
                    "startDate":"2023-12-31T23:00:00",
                    "endDate":"2024-01-01T12:00:00",
                    "timezone": "+01:00",
                    "courseIds":["course1", "course2"],
                    "organisationIds":["1","2"]
                }
                """;
        Course course1 = new Course();
        course1.setId("course1");
        course1.setTitle("Course 1 title");
        Course course2 = new Course();
        course2.setId("course2");
        course2.setTitle("Course 2 title");
        cslStubService.getLearningCatalogue().getCourses(List.of("course1", "course2"), List.of(course1, course2));
        cslStubService.getReportServiceStubService().getCourseCompletionAggregations(
                expectedInput, response
        );
        String reportRequestsResponse = """
                {
                    "requests": []
                }
                """;
        cslStubService.getReportServiceStubService().getReportRequests("userId", List.of("REQUESTED", "PROCESSING"),
                reportRequestsResponse);
        webTestClient
                .post()
                .uri("/admin/reporting/course-completions/generate-graph")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(expectedInput))
                .header("Authorization", "Bearer fakeToken")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.chart[\"2024-01-01T00:00:00\"]").isEqualTo(0)
                .jsonPath("$.chart[\"2024-01-01T01:00:00\"]").isEqualTo(0)
                .jsonPath("$.chart[\"2024-01-01T02:00:00\"]").isEqualTo(0)
                .jsonPath("$.chart[\"2024-01-01T03:00:00\"]").isEqualTo(0)
                .jsonPath("$.chart[\"2024-01-01T04:00:00\"]").isEqualTo(0)
                .jsonPath("$.chart[\"2024-01-01T05:00:00\"]").isEqualTo(0)
                .jsonPath("$.chart[\"2024-01-01T06:00:00\"]").isEqualTo(0)
                .jsonPath("$.chart[\"2024-01-01T07:00:00\"]").isEqualTo(0)
                .jsonPath("$.chart[\"2024-01-01T08:00:00\"]").isEqualTo(0)
                .jsonPath("$.chart[\"2024-01-01T09:00:00\"]").isEqualTo(0)
                .jsonPath("$.chart[\"2024-01-01T10:00:00\"]").isEqualTo(24)
                .jsonPath("$.chart[\"2024-01-01T11:00:00\"]").isEqualTo(70)
                .jsonPath("$.chart[\"2024-01-01T12:00:00\"]").isEqualTo(103)
                .jsonPath("$.chart[\"2024-01-01T13:00:00\"]").isEqualTo(21)
                .jsonPath("$.total").isEqualTo("218")
                .jsonPath("$.timezone").isEqualTo("+01:00")
                .jsonPath("$.delimiter").isEqualTo("hour")
                .jsonPath("$.hasRequest").isEqualTo(false)
                .jsonPath("$.courseBreakdown[\"Course 1 title\"]").isEqualTo("85")
                .jsonPath("$.courseBreakdown[\"Course 2 title\"]").isEqualTo("133");
    }

    @Test
    public void testRequestReport() {
        String reportRequestsResponse = """
                {
                    "addedSuccessfully": true,
                    "details": "addedSuccessfully"
                }
                """;
        String input = """
                {
                    "startDate":"2024-05-08T00:00:00",
                    "endDate":"2024-05-09T00:00:00",
                    "timezone": "Europe/London",
                    "courseIds":["course1", "course2"],
                    "organisationIds":["1","2"],
                    "userEmail": "email",
                    "userId": "id"
                }
                """;
        cslStubService.getReportServiceStubService().postReportRequest(input, reportRequestsResponse);
        webTestClient
                .post()
                .uri("/admin/reporting/course-completions/request-source-data")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(input))
                .header("Authorization", "Bearer fakeToken")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.addedSuccessfully").isEqualTo(true)
                .jsonPath("$.details").isEqualTo("addedSuccessfully");
    }

}
