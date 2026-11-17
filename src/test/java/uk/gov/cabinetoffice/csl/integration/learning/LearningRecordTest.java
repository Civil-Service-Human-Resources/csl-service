package uk.gov.cabinetoffice.csl.integration.learning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEventQuery;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.data.ArrayJsonContentBuilder;
import uk.gov.cabinetoffice.csl.util.data.catalogue.DateRangeJsonValues;
import uk.gov.cabinetoffice.csl.util.data.catalogue.JsonCourseBuilder;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LearningRecordTest extends IntegrationTestBase {

    private static final String ZIP_FILE_NAME = "learner_record_2026-11-13_15-43-26.zip";
    private static final String CSV_FILE_NAME = "learner_record_2026-11-13_15-46-20.csv";
    private static final String CSV_HEADER = "\"userId\",\"userEmail\",\"courseId\",\"courseTitle\",\"eventTimestamp\",\"organisationId\",\"organisationName\",\"professionId\",\"professionName\",\"gradeId\",\"gradeName\"";

    @Autowired
    private CSLStubService cslStubService;

    @Autowired
    private TestDataService testDataService;

    private CivilServant civilServant;

    private byte[] createZipBytes(String csvContent) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry(CSV_FILE_NAME);
            zos.putNextEntry(entry);
            zos.write(csvContent.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
        return baos.toByteArray();
    }

    String requiredLearningMap = """
            {
                "departmentCodeMap": {
                    "CO": ["course1", "course2"]
                }
            }
            """;

    JsonCourseBuilder course1 = JsonCourseBuilder.create("course1", "Course 1")
            .addLinkModule("module1", "module1", false, 30)
            .addDepartmentRequiredLearning("DWP", "2024-01-01T00:00:00Z", "P1Y")
            .addDepartmentRequiredLearning("HMRC", "2023-01-01T00:00:00Z", "P1Y");

    JsonCourseBuilder course2 = JsonCourseBuilder.create("course2", "Course 2")
            .addLinkModule("module1", "module1", false, 0)
            .addFileModule("module2", "module2", false, 0)
            .addDepartmentRequiredLearning("CO", "2024-01-01T00:00:00Z", "P1Y");

    JsonCourseBuilder course3 = JsonCourseBuilder.create("course3", "Course 3")
            .addFaceToFaceModule("module1", "module1", false, 0, "eventId", BigDecimal.valueOf(0L), new DateRangeJsonValues("09:00", "11:00", "2025-01-01"))
            .createBlankAudience();

    String courses = ArrayJsonContentBuilder.create(course1, course2, course3).build();

    @Test
    public void testGetLearningRecord() throws Exception {
        String eventsResponse = """
                {
                    "content": [
                        {
                            "eventTimestamp": "2022-01-01T00:00:00Z",
                            "resourceId": "course1"
                        },
                        {
                            "eventTimestamp": "2023-01-01T00:00:00Z",
                            "resourceId": "course1"
                        },
                        {
                            "eventTimestamp": "2022-01-01T00:00:00Z",
                            "resourceId": "course2"
                        },
                        {
                            "eventTimestamp": "2023-01-01T00:00:00Z",
                            "resourceId": "course3"
                        }
                    ],
                    "totalPages": 1
                }
                """;

        cslStubService.getLearningCatalogue().getMandatoryLearningMap(requiredLearningMap);
        cslStubService.getLearningCatalogue().getCourses(List.of("course1", "course2", "course3"), courses);
        cslStubService.getCsrsStubService().getCivilServant("userId", testDataService.generateCivilServant());
        cslStubService.getLearnerRecord().getLearnerRecordEvents(0, LearnerRecordEventQuery.builder().userId("userId")
                .eventTypes(List.of("COMPLETE_COURSE")).build(), eventsResponse);
        mockMvc.perform(get("/learning/record")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.userId").value("userId"))
                .andExpect(jsonPath("$.requiredLearningRecord.totalRequired").value(2))
                .andExpect(jsonPath("$.requiredLearningRecord.completedCourses.length()").value(1))
                .andExpect(jsonPath("$.requiredLearningRecord.completedCourses[0].id").value("course1"))
                .andExpect(jsonPath("$.requiredLearningRecord.completedCourses[0].title").value("Course 1"))
                .andExpect(jsonPath("$.requiredLearningRecord.completedCourses[0].type").value("link"))
                .andExpect(jsonPath("$.requiredLearningRecord.completedCourses[0].duration").value(30))
                .andExpect(jsonPath("$.requiredLearningRecord.completedCourses[0].completionDate").value("2023-01-01T00:00:00"))
                .andExpect(jsonPath("$.otherLearning[0].id").value("course3"))
                .andExpect(jsonPath("$.otherLearning[0].title").value("Course 3"))
                .andExpect(jsonPath("$.otherLearning[0].type").value("face-to-face"))
                .andExpect(jsonPath("$.otherLearning[0].duration").value(7200))
                .andExpect(jsonPath("$.otherLearning[0].completionDate").value("2023-01-01T00:00:00"));

    }

    @Test
    public void testGetLearnerRecordExportForUserId() throws Exception {
        String csvData = CSV_HEADER + "\n\"user123\",\"user123@example.com\",\"course1\",\"Course 1\",\"2026-11-13T15:46:20Z\",\"org1\",\"Org 1\",\"prof1\",\"Prof 1\",\"g1\",\"Grade 1\"\n";
        byte[] zipBytes = createZipBytes(csvData);
        cslStubService.getReportServiceStubService().exportLearnerRecordForUserId("user123", zipBytes, ZIP_FILE_NAME);

        mockMvc.perform(get("/learning/record/export/user123"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/zip"))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ZIP_FILE_NAME + "\""))
                .andExpect(content().bytes(zipBytes));
    }

    @Test
    public void testGetLearnerRecordExportForLoggedInUser() throws Exception {
        String csvData = CSV_HEADER + "\n\"user123\",\"user123@example.com\",\"course1\",\"Course 1\",\"2026-11-13T15:46:20Z\",\"org1\",\"Org 1\",\"prof1\",\"Prof 1\",\"g1\",\"Grade 1\"\n"
                + "\"user456\",\"user456@example.com\",\"course2\",\"Course 2\",\"2026-11-13T15:46:20Z\",\"org2\",\"Org 2\",\"prof2\",\"Prof 2\",\"g2\",\"Grade 2\"\n";
        byte[] zipBytes = createZipBytes(csvData);
        cslStubService.getReportServiceStubService().exportLearnerRecordForLoggedInUser(zipBytes, ZIP_FILE_NAME);

        mockMvc.perform(get("/learning/record/export"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/zip"))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ZIP_FILE_NAME + "\""))
                .andExpect(content().bytes(zipBytes));
    }
}
