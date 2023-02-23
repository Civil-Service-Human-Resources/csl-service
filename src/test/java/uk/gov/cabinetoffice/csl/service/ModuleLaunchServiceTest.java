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
import java.util.UUID;

import static org.junit.Assert.assertEquals;
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
    private final String courseTitle = "courseTitle";
    private final Boolean isRequired = true;
    private final String moduleId = "moduleId";
    private final String moduleTitle = "moduleTitle";
    private final Boolean optional = false;
    private final String moduleType = "elearning";
    private final String uid = UUID.randomUUID().toString();
    private final String learnerFirstName = "learnerFirstName";
    private final String learnerLastName = "";

    @Test
    public void createLaunchLinkShouldReturnErrorWhenGetCourseRecordForLearnerReturnsError() {
        ResponseEntity errorResponse = createResponseForError("Unable to retrieve course record for the" +
                " learnerId: " + learnerId + ", courseId: " + courseId + ", modules/" +  moduleId,
                "/course_records?userId=" + learnerId + "&courseId=" + courseId);
        when(learnerRecordService.getCourseRecordForLearner(learnerId, courseId)).thenReturn(errorResponse);
        ResponseEntity<?> launchLinkResponse = moduleLaunchService
                .createLaunchLink(createModuleLaunchLinkInput(learnerId, courseId, moduleId));
        assertTrue(launchLinkResponse.getStatusCode().is5xxServerError());
        ErrorResponse responseBody = (ErrorResponse) launchLinkResponse.getBody();
        assert responseBody != null;
        assertEquals("Unable to retrieve module launch link for the learnerId: " + learnerId
                + ", courseId: " + courseId + ", modules/" +  moduleId, responseBody.getMessage());
        assertEquals("/courses/" + courseId + "/modules/" +  moduleId + "/launch", responseBody.getPath());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), responseBody.getError());
    }

    private ModuleLaunchLinkInput createModuleLaunchLinkInput(String learnerId, String courseId, String moduleId) {
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
        moduleRecord.setId(1L);
        moduleRecord.setUid(uid);
        moduleRecord.setModuleId(moduleId);
        moduleRecord.setModuleTitle(moduleTitle);
        moduleRecord.setModuleType(moduleType);
        moduleRecord.setOptional(false);
        moduleRecord.setState(State.IN_PROGRESS);
        moduleRecord.setCreatedAt(LocalDateTime.now());
        moduleRecord.setUpdatedAt(LocalDateTime.now());
        moduleRecord.setCompletionDate(LocalDateTime.now());
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

    private ResponseEntity<?> createResponseForCourseRecordsWithEmptyModuleRecord() {
        CourseRecords courseRecords = createCourseRecords();
        courseRecords.getCourseRecord(courseId).setModuleRecords(new ArrayList<>());
        return new ResponseEntity<>(courseRecords, HttpStatus.OK);
    }

    private ResponseEntity<?> createResponseForCourseRecordsWithEmptyModuleUid() {
        CourseRecords courseRecords = createCourseRecords();
        courseRecords.getCourseRecord(courseId).getModuleRecord(moduleId).setUid(null);
        return new ResponseEntity<>(courseRecords, HttpStatus.OK);
    }

    private RegistrationInput createRegistrationInput() {
        return new RegistrationInput(uid, courseId, moduleId, learnerId, learnerFirstName, learnerLastName);
    }

    private LaunchLink createLaunchLink() {
        return new LaunchLink("https://rustici-engine/RusticiEngine/defaultui/launch.jsp?jwt=eyJ0eXAiOiJKV1");
    }

    private ResponseEntity<?> createResponseForLaunchLink() {
        return new ResponseEntity<>(createLaunchLink(), HttpStatus.OK);
    }
}
