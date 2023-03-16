package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Result;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.rustici.Course;
import uk.gov.cabinetoffice.csl.domain.rustici.Learner;
import uk.gov.cabinetoffice.csl.domain.rustici.RusticiRollupData;
import uk.gov.cabinetoffice.csl.util.CslTestUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ModuleRollupServiceTest {

    @Mock
    private LearnerRecordService learnerRecordService;

    @Mock
    private LearningCatalogueService learningCatalogueService;

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
        moduleRollupService = new ModuleRollupService(learnerRecordService, learningCatalogueService);
        cslTestUtil = new CslTestUtil(learnerRecordService, learnerId, courseId, moduleId, uid,
                currentDateTime, currentDateTime, currentDateTime);
        rusticiRollupData = createRusticiRollupData();
    }

    @Test
    public void invalidRollupDataShouldNotBeProcessed() {
        rusticiRollupData.getCourse().setId(courseId);
        CourseRecord courseRecord = invokeService();
        ModuleRecord updatedModuleRecord = courseRecord != null ? courseRecord.getModuleRecord(moduleId) : null;
        assertNull(updatedModuleRecord);
    }

    @Test
    public void courseRecordShouldBeMarkedCompletedWhenOneOfOneMandatoryModuleIsCompleted() {
        CourseRecord courseRecord = cslTestUtil.createCourseRecord();
        cslTestUtil.mockLearnerRecordServiceForGetCourseRecord(learnerId, courseId, courseRecord);
        mockCourseAndModuleState(courseRecord, State.COMPLETED);
        uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course catalogueCourse = cslTestUtil.createCatalogueCourse();
        mockLearningCatalogueServiceForGetCachedCourse(catalogueCourse);
        CourseRecord updatedCourseRecord = invokeService();
        verify(updatedCourseRecord, State.COMPLETED);
    }

    @Test
    public void courseRecordShouldRemainInProgressWhenOnlyOneOfTwoMandatoryModulesCompleted() {
        CourseRecord courseRecord = cslTestUtil.createCourseRecord();
        cslTestUtil.mockLearnerRecordServiceForGetCourseRecord(learnerId, courseId, courseRecord);
        mockCourseAndModuleState(courseRecord, State.IN_PROGRESS);
        uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course catalogueCourse = cslTestUtil.createCatalogueCourse();
        Module catalogueModule = cslTestUtil.createCatalogueModule();
        catalogueModule.setId("moduleId2");
        catalogueCourse.getModules().add(catalogueModule);
        mockLearningCatalogueServiceForGetCachedCourse(catalogueCourse);
        CourseRecord updatedCourseRecord = invokeService();
        verify(updatedCourseRecord, State.IN_PROGRESS);
    }

    private CourseRecord invokeService() {
        return moduleRollupService.processRusticiRollupData(rusticiRollupData);
    }

    private void verify(CourseRecord updatedCourseRecord, State expectedState) {
        assertNotNull(updatedCourseRecord);
        assertEquals(courseId, updatedCourseRecord.getCourseId());
        assertEquals(rusticiRollupData.getUpdated(), updatedCourseRecord.getLastUpdated());
        assertEquals(expectedState, updatedCourseRecord.getState());

        ModuleRecord updatedModuleRecord = updatedCourseRecord.getModuleRecord(moduleId);
        assertNotNull(updatedModuleRecord);
        assertEquals(moduleId, updatedModuleRecord.getModuleId());
        assertEquals(rusticiRollupData.getUpdated(), updatedModuleRecord.getUpdatedAt());
        assertEquals(rusticiRollupData.getUpdated(), updatedModuleRecord.getCompletionDate());
        assertEquals(State.COMPLETED, updatedModuleRecord.getState());
        assertEquals(Result.PASSED, updatedModuleRecord.getResult());
    }

    private void mockCourseAndModuleState(CourseRecord courseRecord, State courseState) {
        Map<String, String> updateFields = new HashMap<>();
        updateFields.put("updatedAt", rusticiRollupData.getUpdated().toString());
        updateFields.put("completionDate", rusticiRollupData.getCompletedDate().toString());
        updateFields.put("state", State.COMPLETED.name());
        updateFields.put("result", rusticiRollupData.getRegistrationSuccess());

        ModuleRecord moduleRecord = courseRecord.getModuleRecord(moduleId);
        moduleRecord.setUpdatedAt(rusticiRollupData.getUpdated());
        moduleRecord.setCompletionDate(rusticiRollupData.getCompletedDate());
        moduleRecord.setState(State.COMPLETED);
        moduleRecord.setResult(Result.PASSED);
        cslTestUtil.mockLearnerRecordServiceForUpdateModuleRecord(moduleRecord.getId(), updateFields, moduleRecord);

        courseRecord.setState(courseState);
        courseRecord.setLastUpdated(rusticiRollupData.getUpdated());
        cslTestUtil.mockLearnerRecordServiceForUpdateCourseRecordState(
                learnerId, courseId, State.COMPLETED, rusticiRollupData.getUpdated(), courseRecord);
    }

    private void mockLearningCatalogueServiceForGetCachedCourse(
            uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course catalogueCourse) {
        when(learningCatalogueService.getCachedCourse(courseId)).thenReturn(catalogueCourse);
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
