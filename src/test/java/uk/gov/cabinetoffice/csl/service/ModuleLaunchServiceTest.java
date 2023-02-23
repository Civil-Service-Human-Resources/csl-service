package uk.gov.cabinetoffice.csl.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.cabinetoffice.csl.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.returnError;

@RunWith(MockitoJUnitRunner.class)
public class ModuleLaunchServiceTest {

    @Mock
    private LearnerRecordService learnerRecordService;

    @Mock
    private RusticiService rusticiService;

    @InjectMocks
    private ModuleLaunchService moduleLaunchService;

    private final String learnerId = "learnerId";
    private final String courseId = "courseId";
    private final String moduleId = "moduleId";
    private final String courseTitle = "courseTitle";
    private final Boolean isRequired = true;

    @Test
    public void createLaunchLinkShouldReturnErrorWhenGetCourseRecordForLearnerReturnsError() {
        ResponseEntity errorResponse = createResponseForError("Unable to retrieve course record for the" +
                " learnerId: " + learnerId + ", courseId: " + courseId + ", modules/" +  moduleId,
                "/course_records?userId=" + learnerId + "&courseId=" + courseId);
        when(learnerRecordService.getCourseRecordForLearner(learnerId, courseId)).thenReturn(errorResponse);
        ResponseEntity<?> launchLinkResponse = moduleLaunchService
                .createLaunchLink(createModuleLaunchLinkInput(learnerId, courseId, moduleId));
        assertTrue(launchLinkResponse.getStatusCode().is5xxServerError());
    }

    private ModuleLaunchLinkInput createModuleLaunchLinkInput(String learnerId, String courseId, String moduleId) {
        String learnerFirstName = "learnerFirstName";
        String learnerLastName = "learnerLastName";
        ModuleLaunchLinkInput moduleLaunchLinkInput = new ModuleLaunchLinkInput();
        moduleLaunchLinkInput.setLearnerFirstName(learnerFirstName);
        moduleLaunchLinkInput.setLearnerLastName(learnerLastName);
        moduleLaunchLinkInput.setCourseRecordInput(createCourseRecordInput(learnerId, courseId, moduleId));
        return moduleLaunchLinkInput;
    }

    private CourseRecordInput createCourseRecordInput(String learnerId, String courseId, String moduleId) {
        CourseRecordInput courseRecordInput = new CourseRecordInput();
        courseRecordInput.setUserId(learnerId);
        courseRecordInput.setCourseId(courseId);
        courseRecordInput.setCourseTitle(courseTitle);
        courseRecordInput.setIsRequired(isRequired);
        courseRecordInput.setModuleRecords(new ArrayList<>());
        courseRecordInput.getModuleRecords().add(createModuleRecordInput(learnerId, courseId, moduleId));
        return courseRecordInput;
    }

    private ModuleRecordInput createModuleRecordInput(String learnerId, String courseId, String moduleId) {
        String moduleTitle = "moduleTitle";
        Boolean optional = false;
        String moduleType = "moduleType";
        ModuleRecordInput moduleRecordInput = new ModuleRecordInput();
        moduleRecordInput.setUserId(learnerId);
        moduleRecordInput.setCourseId(courseId);
        moduleRecordInput.setModuleId(moduleId);
        moduleRecordInput.setModuleTitle(moduleTitle);
        moduleRecordInput.setOptional(optional);
        moduleRecordInput.setModuleType(moduleType);
        return moduleRecordInput;
    }

    private CourseRecords createCourseRecords() {
        CourseRecords courseRecords = new CourseRecords(new ArrayList<>());
        courseRecords.getCourseRecords().add(createCourseRecord());
        return courseRecords;
    }

    private CourseRecord createCourseRecord() {
        return new CourseRecord(courseId, learnerId, courseTitle, State.IN_PROGRESS, null, null,
                null, isRequired, createModuleRecords(), LocalDateTime.now());
    }

    private List<ModuleRecord> createModuleRecords() {
        List<ModuleRecord> moduleRecords =  new ArrayList<>();
        moduleRecords.add(createModuleRecord());
        return moduleRecords;
    }

    private ModuleRecord createModuleRecord() {
        ModuleRecord moduleRecord = new ModuleRecord();
        //TODO: Populate moduleRecord
        return moduleRecord;
    }

    private ResponseEntity<?> createResponseForError(String message, String path) {
        return returnError(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                message, path, null);
    }

    private ResponseEntity<?> createResponseForCourseRecords() {
        return new ResponseEntity<>(createCourseRecords(), HttpStatus.OK);
    }

    private ResponseEntity<?> createResponseForCourseRecordsWithEmptyCourseRecord() {
        CourseRecords courseRecords = createCourseRecords();
        courseRecords.setCourseRecords(new ArrayList<>());
        return new ResponseEntity<>(courseRecords, HttpStatus.OK);
    }
}
