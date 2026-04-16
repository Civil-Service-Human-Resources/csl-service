package uk.gov.cabinetoffice.csl.integration.learning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEventQuery;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordQuery;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseStatus;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.SearchForCoursesParams;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.data.ArrayJsonContentBuilder;
import uk.gov.cabinetoffice.csl.util.data.catalogue.JsonCourseBuilder;
import uk.gov.cabinetoffice.csl.util.data.learnerRecord.JsonLearnerRecordBuilder;
import uk.gov.cabinetoffice.csl.util.data.learnerRecord.JsonModuleRecordBuilder;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserLearningTest extends IntegrationTestBase {

    @Autowired
    private CSLStubService cslStubService;

    @Autowired
    private TestDataService testDataService;

    String requiredLearningMap = """
            {
                "departmentCodeMap": {
                    "CO": ["course1", "course2"]
                }
            }
            """;

    @Test
    public void testSearchLearning() throws Exception {
        CivilServant civilServant = testDataService.generateCivilServant();
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);
        cslStubService.getLearningCatalogue().getMandatoryLearningMap(requiredLearningMap);

        String learningResourceIdsResponse = """
                {
                    "content": [
                        "course3",
                        "course4",
                        "course5",
                        "course6",
                        "course7"
                    ],
                    "page": 0,
                    "size": 200,
                    "totalElements": 5,
                    "totalPages": 1
                }
                """;

        cslStubService.getLearnerRecord().getLearnerRecordResourceIds(
                LearnerRecordQuery.builder().learnerIds(java.util.Set.of("userId")).notResourceIds(List.of("course1", "course2")).build(),
                0,
                200,
                learningResourceIdsResponse);

        String courses = ArrayJsonContentBuilder.create(
                JsonCourseBuilder.create("course3", "Course 3")
                        .addLinkModule("module1", "module 1", false, 0)
                        .addLinkModule("module2", "module 2", false, 0),
                JsonCourseBuilder.create("course4", "Course 4")
                        .addLinkModule("module3", "module 3", false, 0)
        ).getAsPaginated(0, 20, 1).toString();

        SearchForCoursesParams params = SearchForCoursesParams.builder()
                .query("course").courseIds(List.of("course3", "course4", "course5", "course6", "course7"))
                .status(List.of(CourseStatus.PUBLISHED, CourseStatus.ARCHIVED)).build();

        cslStubService.getLearningCatalogue().postSearchCourses(params, courses, 0, 20);

        String learnerRecordsResponse = ArrayJsonContentBuilder.create(
                JsonLearnerRecordBuilder.create("userId", "course4")
                        .addLatestEvent("COMPLETE_COURSE", "2026-01-01T10:00:00Z"),
                JsonLearnerRecordBuilder.create("userId", "course3")
        ).getAsPaginated(0, 20, 1).toString();

        cslStubService.getLearnerRecord().getLearnerRecords(
                LearnerRecordQuery.builder().learnerIds(java.util.Set.of("userId")).resourceIds(Set.of("course4", "course3")).build(),
                0,
                learnerRecordsResponse);

        String moduleRecordsResponse = ArrayJsonContentBuilder.create(
                JsonModuleRecordBuilder.create("module1", "course3", "userId", "link", "2026-01-01T10:00:00Z")
                        .addCompletionDate("2026-01-01T10:00:00Z", "2026-01-01T10:00:00Z").addState("COMPLETED"),
                JsonModuleRecordBuilder.create("module3", "course4", "userId", "link", "2026-01-01T10:00:00Z")
                        .addCompletionDate("2026-01-01T10:00:00Z", "2026-01-01T10:00:00Z").addState("COMPLETED")
        ).getAsObjectList("moduleRecords").toString();

        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module1", "module2", "module3"), moduleRecordsResponse);

        mockMvc.perform(get("/learning/userId?page=0&size=20&q=course").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.learning.length()").value(2))
                .andExpect(jsonPath("$.learning[0].title").value("Course 3"))
                .andExpect(jsonPath("$.learning[0].status").value("In progress"))
                .andExpect(jsonPath("$.learning[0].completionDate").isEmpty())
                .andExpect(jsonPath("$.learning[1].title").value("Course 4"))
                .andExpect(jsonPath("$.learning[1].status").value("Completed"))
                .andExpect(jsonPath("$.learning[1].completionDate").value("1 Jan 2026"))
                .andExpect(jsonPath("$.totalResults").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    public void testGetLearning() throws Exception {
        CivilServant civilServant = testDataService.generateCivilServant();
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);
        cslStubService.getLearningCatalogue().getMandatoryLearningMap(requiredLearningMap);

        String learnerRecordsResponse = ArrayJsonContentBuilder.create(
                JsonLearnerRecordBuilder.create("userId", "course4").addLatestEvent("COMPLETE_COURSE", "2026-01-01T10:00:00Z"),
                JsonLearnerRecordBuilder.create("userId", "course3")
        ).getAsPaginated(0, 20, 1).toString();

        cslStubService.getLearnerRecord().getLearnerRecordPage(
                LearnerRecordQuery.builder().learnerIds(java.util.Set.of("userId")).notResourceIds(List.of("course1", "course2")).build(),
                0,
                20,
                learnerRecordsResponse);

        String courses = ArrayJsonContentBuilder.create(
                JsonCourseBuilder.create("course3", "B Course 3"),
                JsonCourseBuilder.create("course4", "A Course 4")
        ).build();

        cslStubService.getLearningCatalogue().getCourses(List.of("course4", "course3"), courses);

        mockMvc.perform(get("/learning/userId?page=0&size=20").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.learning.length()").value(2))
                .andExpect(jsonPath("$.learning[0].title").value("A Course 4"))
                .andExpect(jsonPath("$.learning[0].status").value("Completed"))
                .andExpect(jsonPath("$.learning[0].completionDate").value("1 Jan 2026"))
                .andExpect(jsonPath("$.learning[1].title").value("B Course 3"))
                .andExpect(jsonPath("$.learning[1].status").value(""))
                .andExpect(jsonPath("$.learning[1].completionDate").isEmpty())
                .andExpect(jsonPath("$.totalResults").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    public void testGetDetailedLearning() throws Exception {
        CivilServant civilServant = testDataService.generateCivilServant();
        cslStubService.getCsrsStubService().getCivilServant("userId", civilServant);

        String courses = ArrayJsonContentBuilder.create(
                JsonCourseBuilder.create("course1", "Course 1")
                        .addLinkModule("module1", "Module 1", false, 0)
        ).build();

        cslStubService.getLearningCatalogue().getCourses(List.of("course1"), courses);

        cslStubService.getLearningCatalogue().getMandatoryLearningMap("""
                {
                    "departmentCodeMap": {
                        "CO": []
                    }
                }
                """);

        cslStubService.getLearnerRecord().getLearnerRecordEvents(0, LearnerRecordEventQuery.builder().userId("userId").eventTypes(List.of("COMPLETE_COURSE")).build(), """
                {
                    "content": [
                        {
                            "eventTimestamp": "2026-01-01T10:00:00Z",
                            "resourceId": "course1"
                        }
                    ],
                    "totalPages": 1
                }
                """);

        String learnerRecordsResponse = ArrayJsonContentBuilder.create()
                .addElements(
                        JsonLearnerRecordBuilder.create("userId", "course1").addLatestEvent("COMPLETE_COURSE", "2026-01-01T10:00:00Z"),
                        JsonLearnerRecordBuilder.create("userId", "course3")
                ).getAsPaginated(0, 20, 1).toString();

        LearnerRecordQuery query = LearnerRecordQuery.builder()
                .learnerIds(Set.of("userId"))
                .resourceIds(Set.of("course1"))
                .build();

        cslStubService.getLearnerRecord().getLearnerRecords(query, 0, learnerRecordsResponse);

        String moduleRecordsResponse = ArrayJsonContentBuilder.create(
                JsonModuleRecordBuilder.create("module1", "course1", "userId", "link", "2026-01-01T10:00:00Z")
                        .addUpdatedAt("2025-01-01T09:00:00Z").addState("COMPLETED")
        ).getAsObjectList("moduleRecords").toString();

        cslStubService.getLearnerRecord().getModuleRecords(List.of("userId"), List.of("module1"), moduleRecordsResponse);

        mockMvc.perform(get("/learning/detailed/userId?courseIds=course1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.courses.length()").value(1))
                .andExpect(jsonPath("$.courses[0].courseId").value("course1"))
                .andExpect(jsonPath("$.courses[0].courseTitle").value("Course 1"))
                .andExpect(jsonPath("$.courses[0].status").value("COMPLETED"))
                .andExpect(jsonPath("$.courses[0].audience").doesNotExist());
    }
}
