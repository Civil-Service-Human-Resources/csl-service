package uk.gov.cabinetoffice.csl.controller.learning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cabinetoffice.csl.controller.model.ErrorDtoFactory;
import uk.gov.cabinetoffice.csl.service.ReportService;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;
import uk.gov.cabinetoffice.csl.service.learning.LearningRecordService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LearningRecordController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("no-security")
public class LearningRecordControllerTest {

    private static final String ZIP_FILE_NAME = "learner_record_2026-11-13_15-43-26.zip";
    private static final String CSV_FILE_NAME = "learner_record_2026-11-13_15-46-20.csv";
    private static final String CSV_HEADER = "\"userId\",\"userEmail\",\"courseId\",\"courseTitle\",\"eventTimestamp\",\"organisationId\",\"organisationName\",\"professionId\",\"professionName\",\"gradeId\",\"gradeName\"";

    @MockBean
    private LearningRecordService learningRecordService;

    @MockBean
    private IUserAuthService userAuthService;

    @MockBean
    private ReportService reportService;

    @MockBean
    private ErrorDtoFactory errorDtoFactory;

    @Autowired
    private MockMvc mockMvc;

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

    @Test
    public void testGetLearnerRecordExportForUserId() throws Exception {
        String csvData = CSV_HEADER + "\n\"123\",\"user@example.com\",\"c1\",\"Course 1\",\"2026-11-13T15:46:20Z\",\"org1\",\"Org 1\",\"prof1\",\"Prof 1\",\"g1\",\"Grade 1\"\n";
        byte[] zipBytes = createZipBytes(csvData);
        ResponseEntity<byte[]> response = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/zip")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ZIP_FILE_NAME + "\"")
                .body(zipBytes);
        when(reportService.getLearnerRecordExportForUserId("123")).thenReturn(response);

        mockMvc.perform(get("/learning/record/export/123"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/zip"))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ZIP_FILE_NAME + "\""))
                .andExpect(content().bytes(zipBytes));

        verify(reportService).getLearnerRecordExportForUserId("123");
    }

    @Test
    public void testGetLearnerRecordExportForLoggedInUser() throws Exception {
        String csvData = CSV_HEADER + "\n\"123\",\"user1@example.com\",\"c1\",\"Course 1\",\"2026-11-13T15:46:20Z\",\"org1\",\"Org 1\",\"prof1\",\"Prof 1\",\"g1\",\"Grade 1\"\n"
                + "\"456\",\"user2@example.com\",\"c2\",\"Course 2\",\"2026-11-13T15:46:20Z\",\"org2\",\"Org 2\",\"prof2\",\"Prof 2\",\"g2\",\"Grade 2\"\n";
        byte[] zipBytes = createZipBytes(csvData);
        ResponseEntity<byte[]> response = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/zip")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ZIP_FILE_NAME + "\"")
                .body(zipBytes);
        when(reportService.getLearnerRecordExportForLoggedInUser()).thenReturn(response);

        mockMvc.perform(get("/learning/record/export"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/zip"))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ZIP_FILE_NAME + "\""))
                .andExpect(content().bytes(zipBytes));

        verify(reportService).getLearnerRecordExportForLoggedInUser();
    }
}
