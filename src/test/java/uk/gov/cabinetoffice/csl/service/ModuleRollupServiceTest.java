package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.cabinetoffice.csl.domain.rustici.CSLRusticiProps;
import uk.gov.cabinetoffice.csl.domain.rustici.Course;
import uk.gov.cabinetoffice.csl.domain.rustici.Learner;
import uk.gov.cabinetoffice.csl.domain.rustici.RusticiRollupData;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("no-redis")
public class ModuleRollupServiceTest {

    @Mock
    private LearnerRecordService learnerRecordService;

    @Mock
    private RusticiCSLDataService rusticiCSLDataService;

    @InjectMocks
    private ModuleRollupService moduleRollupService;

    private RusticiRollupData rusticiRollupData;

    private final String learnerId = "learnerId";
    private final String courseId = "courseId";
    private final String moduleId = "moduleId";
    private final LocalDateTime currentDateTime = LocalDateTime.now();

    @BeforeEach
    public void setup() {
        rusticiRollupData = createRusticiRollupData();
        reset(learnerRecordService);
    }

    @Test
    public void emptyPatchDataShouldNotBeProcessed() {
        mockGetCSLDataFromRollup(new CSLRusticiProps("", "", "", List.of()));
        invokeService();
        verify(learnerRecordService, never()).getCourseRecord("", "");
        verify(learnerRecordService, never()).updateModuleRecord(any(), any());
        verify(learnerRecordService, never()).updateCourseRecord(any(), any(), any());
    }

    private void invokeService() {
        moduleRollupService.processRusticiRollupData(rusticiRollupData);
    }

    private void mockGetCSLDataFromRollup(CSLRusticiProps returnProps) {
        when(rusticiCSLDataService.getCSLDataFromRollUpData(rusticiRollupData))
                .thenReturn(returnProps);
    }

    private RusticiRollupData createRusticiRollupData() {
        RusticiRollupData rusticiRollupData = new RusticiRollupData();
        rusticiRollupData.setId("rustici_test_course_id");
        rusticiRollupData.setRegistrationCompletion("COMPLETED");
        rusticiRollupData.setRegistrationSuccess("PASSED");
        rusticiRollupData.setUpdated(currentDateTime);
        rusticiRollupData.setCompletedDate(currentDateTime);
        Course course = new Course(courseId + "." + moduleId, "courseTitle", 0);
        rusticiRollupData.setCourse(course);
        Learner learner = new Learner(learnerId, "learnerFirstName", "");
        rusticiRollupData.setLearner(learner);
        return rusticiRollupData;
    }
}
