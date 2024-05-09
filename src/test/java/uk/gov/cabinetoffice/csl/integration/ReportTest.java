package uk.gov.cabinetoffice.csl.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import uk.gov.cabinetoffice.csl.configuration.TestConfig;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationBinDelimiter;
import uk.gov.cabinetoffice.csl.domain.reportservice.GetCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.util.CSLServiceWireMockServer;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.time.LocalDate;
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
                  "delimiter": "hour",
                  "results": [
                    {
                      "courseId": "course1",
                      "total": 10,
                      "dateBin": "2024-01-01T10:00:00Z"
                    },
                    {
                      "courseId": "course2",
                      "total": 14,
                      "dateBin": "2024-01-01T10:00:00Z"
                    },
                    {
                      "courseId": "course1",
                      "total": 50,
                      "dateBin": "2024-01-01T11:00:00Z"
                    },
                    {
                      "courseId": "course2",
                      "total": 20,
                      "dateBin": "2024-01-01T11:00:00Z"
                    },
                    {
                      "courseId": "course1",
                      "total": 13,
                      "dateBin": "2024-01-01T12:00:00Z"
                    },
                    {
                      "courseId": "course2",
                      "total": 90,
                      "dateBin": "2024-01-01T12:00:00Z"
                    },
                    {
                      "courseId": "course1",
                      "total": 12,
                      "dateBin": "2024-01-01T13:00:00Z"
                    },
                    {
                      "courseId": "course2",
                      "total": 9,
                      "dateBin": "2024-01-01T13:00:00Z"
                    }
                  ]
                }
                """;
        GetCourseCompletionsParams expParams = new GetCourseCompletionsParams(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 2), List.of("course1"),
                List.of("1", "2"), List.of(""), List.of(""), AggregationBinDelimiter.HOUR);
        cslStubService.getReportServiceStubService().getCourseCompletionAggregations(
                expParams, response
        );
        webTestClient
                .get()
                .uri("/admin/reporting/course-completions?startDate=2024-01-01&endDate=2024-01-01&courseIds=course1&organisationIds=1,2&binDelimiter=HOUR")
                .header("Authorization", "Bearer fakeToken")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.chart[0].x").isEqualTo("2024-01-01T10:00:00Z[UTC]")
                .jsonPath("$.chart[0].y").isEqualTo("24")
                .jsonPath("$.chart[1].x").isEqualTo("2024-01-01T11:00:00Z[UTC]")
                .jsonPath("$.chart[1].y").isEqualTo("70")
                .jsonPath("$.chart[2].x").isEqualTo("2024-01-01T12:00:00Z[UTC]")
                .jsonPath("$.chart[2].y").isEqualTo("103")
                .jsonPath("$.chart[3].x").isEqualTo("2024-01-01T13:00:00Z[UTC]")
                .jsonPath("$.chart[3].y").isEqualTo("21");
    }

}
