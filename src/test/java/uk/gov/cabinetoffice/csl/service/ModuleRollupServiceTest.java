package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.cabinetoffice.csl.domain.*;
import uk.gov.cabinetoffice.csl.util.CslTestUtil;

import java.time.LocalDateTime;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ModuleRollupServiceTest {

    @Mock
    private LearnerRecordService learnerRecordService;

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
        moduleRollupService = new ModuleRollupService(learnerRecordService);
        cslTestUtil = new CslTestUtil(learnerRecordService, learnerId, courseId, moduleId, uid,
                currentDateTime, currentDateTime, currentDateTime);
        rusticiRollupData = createRusticiRollupData();
    }

    @Test
    public void  testProcessRusticiRollupDataForSuccess() {
        //ModuleRecord moduleRecord = cslTestUtil.createModuleRecord();
        cslTestUtil.mockLearnerRecordServiceForGetCourseRecord(cslTestUtil.createSuccessResponseForCourseRecords());
        //cslTestUtil.mockLearnerRecordServiceForUpdateModuleUpdateDateTime(moduleRecord);
        CourseRecord courseRecord = invokeService();
        ModuleRecord updatedModuleRecord = courseRecord != null ? courseRecord.getModuleRecord(moduleId) : null;
        assertNotNull(updatedModuleRecord);
        assertEquals(moduleId, updatedModuleRecord.getModuleId());
        assertEquals(rusticiRollupData.getUpdated(), updatedModuleRecord.getUpdatedAt());
    }

//    @Test
//    public void  testProcessRusticiRollupDataForFailure() {
//        cslTestUtil.mockLearnerRecordServiceForGetCourseRecord(cslTestUtil.createSuccessResponseForCourseRecords());
//        cslTestUtil.mockLearnerRecordServiceForUpdateModuleUpdateDateTime(null);
//        CourseRecord courseRecord = invokeService();
//        ModuleRecord updatedModuleRecord = courseRecord != null ? courseRecord.getModuleRecord(moduleId) : null;
//        assertNull(updatedModuleRecord);
//    }

    @Test
    public void  testProcessRusticiRollupDataForInvalidRollupData() {
        rusticiRollupData.getCourse().setId(courseId);
        CourseRecord courseRecord = invokeService();
        ModuleRecord updatedModuleRecord = courseRecord != null ? courseRecord.getModuleRecord(moduleId) : null;
        assertNull(updatedModuleRecord);
    }

    private CourseRecord invokeService() {
        return moduleRollupService.processRusticiRollupData(rusticiRollupData);
    }

    private RusticiRollupData createRusticiRollupData() {
        RusticiRollupData rusticiRollupData = new RusticiRollupData();
        rusticiRollupData.setId("rustici_test_course1662455183725");
        rusticiRollupData.setRegistrationCompletion("IN_PROGRESS");
        rusticiRollupData.setRegistrationSuccess("PASSED");
        rusticiRollupData.setUpdated(currentDateTime);
        rusticiRollupData.setCompletedDate(currentDateTime);
        Course course = new Course(courseId + "." + moduleId, "courseTitle", 1);
        rusticiRollupData.setCourse(course);
        Learner learner = new Learner(learnerId, "learnerFirstName", "");
        rusticiRollupData.setLearner(learner);
        return rusticiRollupData;
    }
}
