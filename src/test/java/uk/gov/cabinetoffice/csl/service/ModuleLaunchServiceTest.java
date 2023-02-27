package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.cabinetoffice.csl.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.returnError;

@SpringBootTest
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
    private final String moduleType = "elearning";
    private final String uid = UUID.randomUUID().toString();
    private final String learnerFirstName = "learnerFirstName";
    private final String learnerLastName = "";

    private final String[] disabledBookmarkingModuleIDs = {moduleId, "moduleId1"};

    @BeforeEach
    public void setup() {
        moduleLaunchService = new ModuleLaunchService(learnerRecordService, rusticiService,
                disabledBookmarkingModuleIDs);
    }

    @Test
    public void createLaunchLinkShouldReturnErrorWhenGetCourseRecordForLearnerReturnsError() {
        ResponseEntity errorResponse = createResponseForError("Unable to retrieve course record for the"
                        + " learnerId: " + learnerId + ", courseId: " + courseId + ", moduleId: " +  moduleId,
                "/course_records?userId=" + learnerId + "&courseId=" + courseId);
        when(learnerRecordService.getCourseRecordForLearner(learnerId, courseId)).thenReturn(errorResponse);
        verifyError(invokeService());
    }

    @Test
    public void createLaunchLinkShouldCreateCourseModuleAndThenReturnLaunchLinkWithDisabledBookmark() {
        mockLearnerRecordServiceGetAndUpdateCalls(createResponseForCourseRecordsWithEmptyCourseRecord(),
                createResponseForModuleRecord());
        mockLearnerRecordServiceCreateCalls(
                createCourseRecordInput(learnerId, courseId, moduleId),
                createResponseForCourseRecordWithEmptyModules(),
                createModuleRecordInput(learnerId, courseId, moduleId),
                createResponseForModuleRecord());
        mockRusticiServiceCallGetRegistrationLaunchLink();
        verifySuccessAndLaunchLinkWithDisabledBookmark(invokeService());
    }

    @Test
    public void createLaunchLinkShouldUpdateModuleUidThenReturnLaunchLinkWithDisabledBookmark() {
        mockLearnerRecordServiceGetAndUpdateCalls(createResponseForCourseRecordsWithEmptyModuleUid(),
                createResponseForModuleRecord());
        mockRusticiServiceCallGetRegistrationLaunchLink();
        verifySuccessAndLaunchLinkWithDisabledBookmark(invokeService());
    }

    @Test
    public void createLaunchLinkShouldCreateRegistrationWhenRegistrationIdNotFoundThenReturnLaunchLinkWithDisabledBookmark() {
        mockLearnerRecordServiceGetAndUpdateCalls(createResponseForCourseRecordsWithEmptyModuleUid(),
                createResponseForModuleRecord());
        mockRusticiServiceCallForRegistrationIdNotFoundError();
        mockRusticiServiceCallCreateRegistrationAndLaunchLink();
        verifySuccessAndLaunchLinkWithDisabledBookmark(invokeService());
    }

    private void mockLearnerRecordServiceGetAndUpdateCalls(ResponseEntity responseForCourseRecords,
                                                           ResponseEntity responseForModuleRecord) {
        when(learnerRecordService.getCourseRecordForLearner(learnerId, courseId)).thenReturn(responseForCourseRecords);
        when(learnerRecordService.updateModuleRecordForLearner(any(), any())).thenReturn(responseForModuleRecord);
    }

    private void mockLearnerRecordServiceCreateCalls(
            CourseRecordInput courseRecordInput, ResponseEntity responseForCourseRecord,
            ModuleRecordInput moduleRecordInput, ResponseEntity responseForModuleRecord) {
        when(learnerRecordService.createCourseRecordForLearner(courseRecordInput)).thenReturn(responseForCourseRecord);
        when(learnerRecordService.createModuleRecordForLearner(moduleRecordInput)).thenReturn(responseForModuleRecord);
    }

    private void mockRusticiServiceCallGetRegistrationLaunchLink() {
        ResponseEntity responseForLaunchLink = createRusticiResponseForLaunchLink();
        when(rusticiService.getRegistrationLaunchLink(createRegistrationInput())).thenReturn(responseForLaunchLink);
    }

    private void mockRusticiServiceCallCreateRegistrationAndLaunchLink() {
        ResponseEntity responseForLaunchLink = createRusticiResponseForLaunchLink();
        when(rusticiService.createRegistrationAndLaunchLink(createRegistrationInput())).thenReturn(responseForLaunchLink);
    }

    private void mockRusticiServiceCallForRegistrationIdNotFoundError() {
        ResponseEntity rusticiResponseRegistrationIdNotFoundError = createRusticiResponseRegistrationIdNotFoundError();
        when(rusticiService.getRegistrationLaunchLink(createRegistrationInput())).thenReturn(rusticiResponseRegistrationIdNotFoundError);
    }

    private ResponseEntity<?> invokeService() {
        return moduleLaunchService.createLaunchLink(createModuleLaunchLinkInput(learnerId, courseId, moduleId));
    }

    private void verifyError(ResponseEntity<?> launchLinkResponse) {
        assertTrue(launchLinkResponse.getStatusCode().is5xxServerError());
        ErrorResponse responseBody = (ErrorResponse) launchLinkResponse.getBody();
        assert responseBody != null;
        assertEquals("Unable to retrieve module launch link for the learnerId: " + learnerId
                + ", courseId: " + courseId + ", moduleId: " +  moduleId, responseBody.getMessage());
        assertEquals("/courses/" + courseId + "/modules/" +  moduleId + "/launch", responseBody.getPath());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), responseBody.getError());
    }

    private void verifySuccessAndLaunchLinkWithDisabledBookmark(ResponseEntity<?> launchLinkResponse) {
        assertTrue(launchLinkResponse.getStatusCode().is2xxSuccessful());
        LaunchLink actualLaunchLink = (LaunchLink) launchLinkResponse.getBody();
        LaunchLink expectedLaunchLink = createLaunchLink();
        expectedLaunchLink.setLaunchLink(expectedLaunchLink.getLaunchLink() + "&clearbookmark=true");
        assertEquals(expectedLaunchLink, actualLaunchLink);
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
        courseRecordInput.setState(State.IN_PROGRESS.name());
        courseRecordInput.setIsRequired(isRequired);
        courseRecordInput.setModuleRecords(new ArrayList<>());
        courseRecordInput.getModuleRecords().add(createModuleRecordInput(learnerId, courseId, moduleId));
        return courseRecordInput;
    }

    private ModuleRecordInput createModuleRecordInput(String learnerId, String courseId, String moduleId) {
        ModuleRecordInput moduleRecordInput = new ModuleRecordInput();
        moduleRecordInput.setUid(uid);
        moduleRecordInput.setUserId(learnerId);
        moduleRecordInput.setCourseId(courseId);
        moduleRecordInput.setModuleId(moduleId);
        moduleRecordInput.setModuleTitle(moduleTitle);
        moduleRecordInput.setOptional(false);
        moduleRecordInput.setModuleType(moduleType);
        moduleRecordInput.setState(State.IN_PROGRESS.name());
        return moduleRecordInput;
    }

    private ResponseEntity<?> createResponseForError(String message, String path) {
        return returnError(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                message, path, null);
    }

    private CourseRecords createCourseRecords() {
        CourseRecords courseRecords = new CourseRecords(new ArrayList<>());
        courseRecords.getCourseRecords().add(createCourseRecord());
        return courseRecords;
    }

    private ResponseEntity<?> createResponseForCourseRecordsWithEmptyModuleUid() {
        CourseRecords courseRecords = createCourseRecords();
        courseRecords.getCourseRecord(courseId).getModuleRecord(moduleId).setUid(null);
        return new ResponseEntity<>(courseRecords, HttpStatus.OK);
    }

    private ResponseEntity<?> createResponseForCourseRecordsWithEmptyCourseRecord() {
        CourseRecords courseRecords = createCourseRecords();
        courseRecords.setCourseRecords(null);
        return new ResponseEntity<>(courseRecords, HttpStatus.OK);
    }

    private CourseRecord createCourseRecord() {
        return new CourseRecord(courseId, learnerId, courseTitle, State.IN_PROGRESS, null, null,
                null, isRequired, createModuleRecords(), LocalDateTime.now());
    }

    private ResponseEntity<?> createResponseForCourseRecordWithEmptyModules() {
        CourseRecord courseRecord = createCourseRecord();
        courseRecord.setModuleRecords(null);
        return new ResponseEntity<>(courseRecord, HttpStatus.OK);
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

    private ResponseEntity<?> createResponseForModuleRecord() {
        return new ResponseEntity<>(createModuleRecord(), HttpStatus.OK);
    }

    private RegistrationInput createRegistrationInput() {
        return new RegistrationInput(uid, courseId, moduleId, learnerId, learnerFirstName, learnerLastName);
    }

    private LaunchLink createLaunchLink() {
        return new LaunchLink("https://rustici-engine/RusticiEngine/defaultui/launch.jsp?jwt=eyJ0eXAiOiJKV1");
    }

    private ResponseEntity<?> createRusticiResponseForLaunchLink() {
        return new ResponseEntity<>(createLaunchLink(), HttpStatus.OK);
    }

    private ErrorResponse rusticiErrorForRegistrationNotFound() {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("Registration ID 'registration_id' does not exist");
        return errorResponse;
    }

    private ResponseEntity<?> createRusticiResponseRegistrationIdNotFoundError() {
        return new ResponseEntity<>(rusticiErrorForRegistrationNotFound(), HttpStatus.NOT_FOUND);
    }
}
