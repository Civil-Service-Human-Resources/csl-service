package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.rustici.CSLRusticiProps;
import uk.gov.cabinetoffice.csl.domain.rustici.Course;
import uk.gov.cabinetoffice.csl.domain.rustici.Learner;
import uk.gov.cabinetoffice.csl.domain.rustici.RusticiRollupData;
import uk.gov.cabinetoffice.csl.util.CslTestUtil;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//@SpringBootTest(classes = ModuleRollupService.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("no-redis")
public class ModuleRollupServiceTest {

    @Mock
    private LearnerRecordService learnerRecordService;

    @Mock
    private RusticiCSLDataService rusticiCSLDataService;

    @InjectMocks
    private ModuleRollupService moduleRollupService;

    private CslTestUtil cslTestUtil;

    private RusticiRollupData rusticiRollupData;

    private final String learnerId = "learnerId";
    private final String courseId = "courseId";
    private final String moduleId = "moduleId";
    private final String uid = randomUUID().toString();
    private final LocalDateTime currentDateTime = LocalDateTime.now();

    @BeforeEach
    public void setup() {
//        moduleRollupService = new ModuleRollupService(learnerRecordService, rusticiCSLDataService);
        cslTestUtil = new CslTestUtil(learnerRecordService, learnerId, courseId, moduleId, uid,
                currentDateTime, currentDateTime, currentDateTime);
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

    @Test
    public void shouldApplyPatchesToModuleRecordOnly() {
        CourseRecord courseRecord = cslTestUtil.createCourseRecord();
        ModuleRecord moduleRecord = courseRecord.getModuleRecord(moduleId);
        List<PatchOp> expPatches = List.of(
                PatchOp.replacePatch("completionDate", rusticiRollupData.getCompletedDate().toString())
        );
        mockGetCSLDataFromRollup(new CSLRusticiProps(courseId, moduleId, learnerId, expPatches));
        when(learnerRecordService.getCourseRecord(learnerId, courseId)).thenReturn(courseRecord);
        when(learnerRecordService.updateModuleRecord(moduleRecord.getId(), expPatches)).thenReturn(moduleRecord);
        when(learnerRecordService.isCourseCompleted(courseId, List.of())).thenReturn(false);
        invokeService();
        verify(learnerRecordService, never()).updateCourseRecord(any(), any(), any());
    }

    @Test
    public void shouldApplyPatchesToModuleRecordAndCompleteCourseRecord() {
        CourseRecord courseRecord = cslTestUtil.createCourseRecord();
        ModuleRecord moduleRecord = courseRecord.getModuleRecord(moduleId);
        List<PatchOp> expPatches = List.of(
                PatchOp.replacePatch("completionDate", rusticiRollupData.getCompletedDate().toString())
        );
        moduleRecord.setState(State.COMPLETED);
        mockGetCSLDataFromRollup(new CSLRusticiProps(courseId, moduleId, learnerId, expPatches));
        when(learnerRecordService.getCourseRecord(learnerId, courseId)).thenReturn(courseRecord);
        when(learnerRecordService.updateModuleRecord(moduleRecord.getId(), expPatches)).thenReturn(moduleRecord);
        when(learnerRecordService.isCourseCompleted(courseId, List.of(moduleId))).thenReturn(true);
        invokeService();
        verify(learnerRecordService, atMostOnce()).updateCourseRecord(learnerId, courseId,
                List.of(PatchOp.replacePatch("state", "COMPLETED")));
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
