package uk.gov.cabinetoffice.csl.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.csrs.record.OrganisationalUnitsPagedResponse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
public class ReportTest extends IntegrationTestBase {

    @Autowired
    private CSLStubService cslStubService;
    @Autowired
    private TestDataService testDataService;

    @Test
    public void testGetAggregations() throws Exception {
        String response = """
                {
                  "timezone": "+01:00",
                  "delimiter": "hour",
                  "results": [
                    {
                      "total": 10,
                      "dateBin": "2024-01-01T10:00:00"
                    },
                    {
                      "total": 14,
                      "dateBin": "2024-01-01T11:00:00"
                    },
                    {
                      "total": 13,
                      "dateBin": "2024-01-01T12:00:00"
                    },
                    {
                      "total": 12,
                      "dateBin": "2024-01-01T13:00:00"
                    }
                  ]
                }
                """;
        String expectedInputForCompletionAggregations = """
                {
                    "startDate":"2023-12-31T23:00:00",
                    "endDate":"2024-01-01T12:00:00",
                    "timezone": "+01:00",
                    "organisationIds":["1","2"]
                }
                """;

        String expectedInputForGenerateGraph = """
                {
                    "startDate":"2023-12-31T23:00:00",
                    "endDate":"2024-01-01T12:00:00",
                    "timezone": "+01:00",
                    "selectedOrganisationIds":["1","2"]
                }
                """;

        OrganisationalUnit org1 = new OrganisationalUnit();
        org1.setId(1L);
        org1.setName("Org1");
        OrganisationalUnit org2 = new OrganisationalUnit();
        org2.setId(2L);
        org2.setName("Org2");
        List<OrganisationalUnit> orgs = new ArrayList<>();
        orgs.add(org1);
        orgs.add(org2);

        OrganisationalUnitsPagedResponse organisationalUnitsPagedResponse = new OrganisationalUnitsPagedResponse();
        organisationalUnitsPagedResponse.setContent(orgs);
        organisationalUnitsPagedResponse.setTotalPages(1);
        organisationalUnitsPagedResponse.setTotalElements(2);
        organisationalUnitsPagedResponse.setSize(2);

        cslStubService.getCsrsStubService().getOrganisations(organisationalUnitsPagedResponse);
        cslStubService.getReportServiceStubService().getCourseCompletionAggregations(
                expectedInputForCompletionAggregations, response
        );

        mockMvc.perform(post("/admin/reporting/course-completions/generate-graph")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedInputForGenerateGraph))
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
                .andExpect(jsonPath("$.chart[\"2024-01-01T10:00:00\"]").value(10))
                .andExpect(jsonPath("$.chart[\"2024-01-01T11:00:00\"]").value(14))
                .andExpect(jsonPath("$.chart[\"2024-01-01T12:00:00\"]").value(13))
                .andExpect(jsonPath("$.chart[\"2024-01-01T13:00:00\"]").value(12))
                .andExpect(jsonPath("$.total").value("49"))
                .andExpect(jsonPath("$.timezone").value("+01:00"))
                .andExpect(jsonPath("$.delimiter").value("hour"))
                .andExpect(jsonPath("$.hasRequest").value(false));
    }

    @Test
    public void testGetAggregationsByCourse() throws Exception {
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

        String expectedInputForCompletionAggregations = """
                {
                    "startDate":"2023-12-31T23:00:00",
                    "endDate":"2024-01-01T12:00:00",
                    "timezone": "+01:00",
                    "organisationIds":["1","2"],
                    "courseIds":["course1", "course2"]
                }
                """;

        String expectedInputForGenerateGraph = """
                {
                    "startDate":"2023-12-31T23:00:00",
                    "endDate":"2024-01-01T12:00:00",
                    "timezone": "+01:00",
                    "selectedOrganisationIds":["1"],
                    "courseIds":["course1", "course2"]
                }
                """;

        OrganisationalUnitsPagedResponse organisationalUnitsPagedResponse = testDataService.generateOrganisationalUnitsPagedResponse();

        cslStubService.getCsrsStubService().getOrganisations(organisationalUnitsPagedResponse);

        Course course1 = new Course();
        course1.setId("course1");
        course1.setTitle("Course 1 title");
        Course course2 = new Course();
        course2.setId("course2");
        course2.setTitle("Course 2 title");
        cslStubService.getLearningCatalogue().getCourses(List.of("course1", "course2"), List.of(course1, course2));

        cslStubService.getReportServiceStubService().getCourseCompletionAggregationsByCourse(
                expectedInputForCompletionAggregations, response
        );
        mockMvc.perform(post("/admin/reporting/course-completions/generate-graph")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedInputForGenerateGraph))
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
                .andExpect(jsonPath("$.breakdowns[0][\"total\"]").value("218"))
                .andExpect(jsonPath("$.breakdowns[0][\"rows\"][\"Course 1 title\"]").value("85"))
                .andExpect(jsonPath("$.breakdowns[0][\"rows\"][\"Course 2 title\"]").value("133"))
                .andExpect(jsonPath("$.breakdowns[0][\"title\"]").value("Course breakdown"));
    }

    @Test
    public void testGetAggregationsByCourseAndOrganisation() throws Exception {
        String org3Response = """
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
                      "courseId": "course1",
                      "total": 7,
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
                      "courseId": "course1",
                      "total": 6,
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

        String org4Response = """
                {
                  "timezone": "+01:00",
                  "delimiter": "hour",
                  "results": [
                    {
                      "courseId": "course1",
                      "total": 7,
                      "organisationId": "4",
                      "dateBin": "2024-01-01T10:00:00"
                    },
                    {
                      "courseId": "course1",
                      "total": 50,
                      "organisationId": "4",
                      "dateBin": "2024-01-01T11:00:00"
                    },
                    {
                      "courseId": "course2",
                      "total": 20,
                      "organisationId": "4",
                      "dateBin": "2024-01-01T11:00:00"
                    },
                    {
                      "courseId": "course1",
                      "total": 6,
                      "organisationId": "4",
                      "dateBin": "2024-01-01T12:00:00"
                    },
                    {
                      "courseId": "course1",
                      "total": 12,
                      "organisationId": "4",
                      "dateBin": "2024-01-01T13:00:00"
                    }
                  ]
                }
                """;

        String expectedInputForCompletionAggregationsOrg4 = """
                {
                    "startDate":"2023-12-31T23:00:00",
                    "endDate":"2024-01-01T12:00:00",
                    "timezone": "+01:00",
                    "organisationIds":[4],
                    "courseIds":["course1", "course2"]
                }
                """;

        String expectedInputForCompletionAggregationsOrg3 = """
                {
                    "startDate":"2023-12-31T23:00:00",
                    "endDate":"2024-01-01T12:00:00",
                    "timezone": "+01:00",
                    "organisationIds":[3, 4],
                    "courseIds":["course1", "course2"]
                }
                """;

        String expectedInputForGenerateGraph = """
                {
                    "startDate":"2023-12-31T23:00:00",
                    "endDate":"2024-01-01T12:00:00",
                    "timezone": "+01:00",
                    "selectedOrganisationIds":["3", "4"],
                    "courseIds":["course1", "course2"]
                }
                """;

        OrganisationalUnitsPagedResponse organisationalUnitsPagedResponse = testDataService.generateOrganisationalUnitsPagedResponse();

        cslStubService.getCsrsStubService().getOrganisations(organisationalUnitsPagedResponse);

        Course course1 = new Course();
        course1.setId("course1");
        course1.setTitle("Course 1 title");
        Course course2 = new Course();
        course2.setId("course2");
        course2.setTitle("Course 2 title");
        cslStubService.getLearningCatalogue().getCourses(List.of("course1", "course2"), List.of(course1, course2));

        cslStubService.getReportServiceStubService().getCourseCompletionAggregationsByCourse(
                expectedInputForCompletionAggregationsOrg4, org4Response
        );

        cslStubService.getReportServiceStubService().getCourseCompletionAggregationsByCourse(
                expectedInputForCompletionAggregationsOrg3, org3Response
        );
        mockMvc.perform(post("/admin/reporting/course-completions/generate-graph")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedInputForGenerateGraph))
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
                .andExpect(jsonPath("$.chart[\"2024-01-01T10:00:00\"]").value(31))
                .andExpect(jsonPath("$.chart[\"2024-01-01T11:00:00\"]").value(70))
                .andExpect(jsonPath("$.chart[\"2024-01-01T12:00:00\"]").value(109))
                .andExpect(jsonPath("$.chart[\"2024-01-01T13:00:00\"]").value(21))
                .andExpect(jsonPath("$.total").value("231"))
                .andExpect(jsonPath("$.timezone").value("+01:00"))
                .andExpect(jsonPath("$.delimiter").value("hour"))
                .andExpect(jsonPath("$.hasRequest").value(false))
                .andExpect(jsonPath("$.breakdowns[0][\"total\"]").value("136"))
                .andExpect(jsonPath("$.breakdowns[0][\"rows\"][\"Course 1 title\"]").value("23"))
                .andExpect(jsonPath("$.breakdowns[0][\"rows\"][\"Course 2 title\"]").value("113"))
                .andExpect(jsonPath("$.breakdowns[0][\"title\"]").value("OrgName1 (OName1) | OrgName2 | OrgName3 (OName3)"))
                .andExpect(jsonPath("$.breakdowns[1][\"total\"]").value("95"))
                .andExpect(jsonPath("$.breakdowns[1][\"rows\"][\"Course 1 title\"]").value("75"))
                .andExpect(jsonPath("$.breakdowns[1][\"rows\"][\"Course 2 title\"]").value("20"))
                .andExpect(jsonPath("$.breakdowns[1][\"title\"]").value("OrgName1 (OName1) | OrgName2 | OrgName3 (OName3) | OrgName4 (OName4)"));
    }

    @Test
    public void testRequestReport() throws Exception {
        String reportRequestsResponse = """
                {
                    "addedSuccessfully": true,
                    "details": "addedSuccessfully"
                }
                """;
        String inputForReportRequest = """
                {
                    "startDate":"2024-05-08T00:00:00",
                    "endDate":"2024-05-09T00:00:00",
                    "timezone": "Europe/London",
                    "courseIds":["course1", "course2"],
                    "organisationIds":["1","2"],
                    "userEmail": "email",
                    "userId": "id",
                    "downloadBaseUrl": "http://localhost:3005/download"
                }
                """;

        String inputForRequestSourceData = """
                {
                    "startDate":"2024-05-08T00:00:00",
                    "endDate":"2024-05-09T00:00:00",
                    "timezone": "Europe/London",
                    "courseIds":["course1", "course2"],
                    "selectedOrganisationIds":["1","2"],
                    "userEmail": "email",
                    "userId": "id",
                    "downloadBaseUrl": "http://localhost:3005/download"
                }
                """;

        OrganisationalUnit org1 = new OrganisationalUnit();
        org1.setId(1L);
        org1.setName("Org1");
        OrganisationalUnit org2 = new OrganisationalUnit();
        org2.setId(2L);
        org2.setName("Org2");
        List<OrganisationalUnit> orgs = new ArrayList<>();
        orgs.add(org1);
        orgs.add(org2);

        OrganisationalUnitsPagedResponse organisationalUnitsPagedResponse = new OrganisationalUnitsPagedResponse();
        organisationalUnitsPagedResponse.setContent(orgs);
        organisationalUnitsPagedResponse.setTotalPages(1);
        organisationalUnitsPagedResponse.setTotalElements(2);
        organisationalUnitsPagedResponse.setSize(2);

        cslStubService.getCsrsStubService().getOrganisations(organisationalUnitsPagedResponse);
        cslStubService.getReportServiceStubService().postReportRequest(inputForReportRequest, reportRequestsResponse);
        mockMvc.perform(post("/admin/reporting/course-completions/request-source-data")
                        .with(jwt().authorities(new SimpleGrantedAuthority("REPORT_EXPORT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputForRequestSourceData))
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

    @Test
    public void testDownloadReportNotFound() throws Exception {
        String testSlug = "testSlug";
        cslStubService.getReportServiceStubService().downloadCourseCompletionReportNotFound(testSlug);
        mockMvc.perform(get(String.format("/admin/reporting/course-completions/download-report/%s", testSlug))
                        .with(jwt().authorities(new SimpleGrantedAuthority("REPORT_EXPORT"))))
                .andExpect(status().isNotFound());
    }

}
