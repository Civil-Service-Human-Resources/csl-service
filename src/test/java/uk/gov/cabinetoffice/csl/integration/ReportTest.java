package uk.gov.cabinetoffice.csl.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
public class ReportTest extends IntegrationTestBase {

    @Autowired
    private CSLStubService cslStubService;

    @Test
    public void testGetAggregations() throws Exception {
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
        mockMvc.perform(post("/admin/reporting/course-completions/generate-graph")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedInput))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.chart[\"2024-01-01T00:00:00\"]").value(0))
                .andExpect(jsonPath("$.chart[\"2024-01-01T00:00:00\"]").value(0))
                .andExpect(jsonPath("$.chart[\"2024-01-01T01:00:00\"]").value(0))
                .andExpect(jsonPath("$.chart[\"2024-01-01T02:00:00\"]").value(0))
                .andExpect(jsonPath("$.chart[\"2024-01-01T03:00:00\"]").value(0))
                .andExpect(jsonPath("$.chart[\"2024-01-01T04:00:00\"]").value(0))
                .andExpect(jsonPath("$.chart[\"2024-01-01T05:00:00\"]").value(0))
                .andExpect(jsonPath("$.chart[\"2024-01-01T06:00:00\"]").value(0))
                .andExpect(jsonPath("$.chart[\"2024-01-01T07:00:00\"]").value(0))
                .andExpect(jsonPath("$.chart[\"2024-01-01T08:00:00\"]").value(0))
                .andExpect(jsonPath("$.chart[\"2024-01-01T09:00:00\"]").value(0))
                .andExpect(jsonPath("$.chart[\"2024-01-01T10:00:00\"]").value(24))
                .andExpect(jsonPath("$.chart[\"2024-01-01T11:00:00\"]").value(70))
                .andExpect(jsonPath("$.chart[\"2024-01-01T12:00:00\"]").value(103))
                .andExpect(jsonPath("$.chart[\"2024-01-01T13:00:00\"]").value(21))
                .andExpect(jsonPath("$.total").value("218"))
                .andExpect(jsonPath("$.timezone").value("+01:00"))
                .andExpect(jsonPath("$.delimiter").value("hour"))
                .andExpect(jsonPath("$.hasRequest").value(false))
                .andExpect(jsonPath("$.courseBreakdown[\"Course 1 title\"]").value("85"))
                .andExpect(jsonPath("$.courseBreakdown[\"Course 2 title\"]").value("133"));
    }

    @Test
    public void testRequestReport() throws Exception {
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
                    "userId": "id",
                    "downloadBaseUrl": "https://www.base.learn.civilservice.gov.uk"
                }
                """;
        cslStubService.getReportServiceStubService().postReportRequest(input, reportRequestsResponse);
        mockMvc.perform(post("/admin/reporting/course-completions/request-source-data")
                        .with(jwt().authorities(new SimpleGrantedAuthority("REPORT_EXPORT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.addedSuccessfully").value(true))
                .andExpect(jsonPath("$.details").value("addedSuccessfully"));
    }

    @Test
    public void testDownloadReport() throws Exception {
        String testContent = "content";
        String testSlug = "testSlug";
        String filename = "file.txt";
        cslStubService.getReportServiceStubService().downloadCourseCompletionReport(testSlug, filename, testContent);
        mockMvc.perform(get(String.format("/admin/reporting/course-completions/download-report/%s", testSlug))
                        .with(jwt().authorities(new SimpleGrantedAuthority("REPORT_EXPORT"))))
                .andExpect(status().is2xxSuccessful())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", filename)))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(testContent.getBytes()));
    }

}
