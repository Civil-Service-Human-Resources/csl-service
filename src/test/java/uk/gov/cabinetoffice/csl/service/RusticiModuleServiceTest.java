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

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.convertObjectToJsonString;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.createInternalServerErrorResponse;

@SpringBootTest
public class RusticiModuleServiceTest {

    @Mock
    private LearnerRecordService learnerRecordService;

    @Mock
    private RusticiService rusticiService;

    @InjectMocks
    private RusticiModuleService rusticiModuleService;

    private final String learnerId = "learnerId";
    private final String courseId = "courseId";
    private final String courseTitle = "courseTitle";
    private final Boolean isRequired = true;
    private final String moduleId = "moduleId";
    private final String moduleTitle = "moduleTitle";
    private final String moduleType = "elearning";
    private final String uid = randomUUID().toString();
    private final String learnerFirstName = "learnerFirstName";
    private final String learnerLastName = "";

    private final String[] disabledBookmarkingModuleIDs = {moduleId, "moduleId1"};

    @BeforeEach
    public void setup() {
        rusticiModuleService = new RusticiModuleService(learnerRecordService, rusticiService, disabledBookmarkingModuleIDs);
    }

    @Test
    public void createLaunchLinkShouldCreateCourseAndModuleAndThenReturnLaunchLinkWithDisabledBookmark() {
        mockLearnerRecordServiceGetAndUpdateCalls(createSuccessResponseForCourseRecordsWithEmptyCourseRecord(),
                createSuccessResponseForModuleRecord());
        mockLearnerRecordServiceForCreateInProgressCourseRecordWithModuleRecord(
                createCourseRecordInput(learnerId, courseId, moduleId), createCourseRecord());
        mockLearnerRecordServiceCreateCalls(
                createCourseRecordInput(learnerId, courseId, moduleId),
                createSuccessResponseForCourseRecordWithEmptyModules(),
                createModuleRecordInput(learnerId, courseId, moduleId),
                createSuccessResponseForModuleRecord());
        mockRusticiServiceCallGetRegistrationLaunchLink();
        verifySuccessAndLaunchLinkWithDisabledBookmark(invokeService());
    }

    @Test
    public void createLaunchLinkShouldUpdateModuleUidThenReturnLaunchLinkWithDisabledBookmark() {
        CourseRecords courseRecords = createCourseRecords();
        mockLearnerRecordServiceGetAndUpdateCalls(createSuccessResponseForCourseRecordsWithEmptyModuleUid(courseRecords),
                createSuccessResponseForModuleRecord());
        ModuleRecord moduleRecord = courseRecords.getCourseRecord(courseId).getModuleRecord(moduleId);
        mockLearnerRecordServiceForUpdateModuleRecordToAssignUid(moduleRecord, learnerId, courseId);
        mockRusticiServiceCallGetRegistrationLaunchLink();
        verifySuccessAndLaunchLinkWithDisabledBookmark(invokeService());
    }

    @Test
    public void createLaunchLinkShouldCreateRegistrationWhenRegistrationIdNotFoundThenReturnLaunchLinkWithDisabledBookmark() {
        CourseRecords courseRecords = createCourseRecords();
        mockLearnerRecordServiceGetAndUpdateCalls(createSuccessResponseForCourseRecordsWithEmptyModuleUid(courseRecords),
                createSuccessResponseForModuleRecord());
        ModuleRecord moduleRecord = courseRecords.getCourseRecord(courseId).getModuleRecord(moduleId);
        mockLearnerRecordServiceForUpdateModuleRecordToAssignUid(moduleRecord, learnerId, courseId);
        mockRusticiServiceCallForRegistrationIdNotFoundError();
        mockRusticiServiceCallCreateRegistrationAndLaunchLink();
        verifySuccessAndLaunchLinkWithDisabledBookmark(invokeService());
    }

    @Test
    public void createLaunchLinkShouldReturnErrorWhenGetCourseRecordReturnsError() {
        mockLearnerRecordServiceForGetCourseRecord(createErrorResponseForCourseRecordsWithEmptyCourseRecord());
        verify5xxError(invokeService());
    }

    @Test
    public void createLaunchLinkShouldReturnErrorWhenCreateCourseRecordReturnsError() {
        mockLearnerRecordServiceForGetCourseRecord(createSuccessResponseForCourseRecordsWithEmptyCourseRecord());
        mockLearnerRecordServiceForCreateCourseRecord(createCourseRecordInput(learnerId, courseId, moduleId),
                createErrorResponseForCourseRecordsWithEmptyCourseRecord());
        verify5xxError(invokeService());
    }

    @Test
    public void createLaunchLinkShouldReturnErrorWhenCreateModuleRecordReturnsError() {
        mockLearnerRecordServiceForGetCourseRecord(createSuccessResponseForCourseRecordsWithEmptyModule());
        mockLearnerRecordServiceForCreateModuleRecord(createModuleRecordInput(learnerId, courseId, moduleId),
                createInternalServerErrorResponse());
        verify5xxError(invokeService());
    }

    @Test
    public void createLaunchLinkShouldReturnErrorWhenUpdateModuleRecordUidReturnsError() {
        CourseRecords courseRecords = createCourseRecords();
        mockLearnerRecordServiceForGetCourseRecord(createSuccessResponseForCourseRecordsWithEmptyModuleUid(courseRecords));
        mockLearnerRecordServiceForUpdateModuleRecord(createInternalServerErrorResponse());
        verify5xxError(invokeService());
    }

    @Test
    public void createLaunchLinkShouldReturnErrorWhenRegistrationIdNotFoundAndThenNotAbleToCreateRegistrationInRustici() {
        mockLearnerRecordServiceGetAndUpdateCalls(createSuccessResponseForCourseRecords(),
                createSuccessResponseForModuleRecord());
        mockRusticiServiceCallForRegistrationIdNotFoundError();
        mockRusticiServiceCallForInvalidCourseIdBadRequestError();
        String invalidCourseIdMessage = "Course ID '" + courseId + "." + moduleId + "' is invalid";
        verify4xxError(invalidCourseIdMessage, invokeService());
    }

    private ResponseEntity<?> invokeService() {
        return rusticiModuleService.createLaunchLink(createModuleLaunchLinkInput(learnerId, courseId, moduleId));
    }

    private void verify5xxError(ResponseEntity<?> launchLinkResponse) {
        assertTrue(launchLinkResponse.getStatusCode().is5xxServerError());
        ErrorResponse responseBody = (ErrorResponse) launchLinkResponse.getBody();
        assert responseBody != null;
        assertEquals("Unable to retrieve module launch link for the learnerId: " + learnerId
                + ", courseId: " + courseId + " and moduleId: " +  moduleId, responseBody.getMessage());
        assertEquals("/courses/" + courseId + "/modules/" +  moduleId + "/launch", responseBody.getPath());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), responseBody.getError());
    }

    private void verify4xxError(String expectedMessage, ResponseEntity<?> launchLinkResponse) {
        assertTrue(launchLinkResponse.getStatusCode().is4xxClientError());
        ErrorResponse responseBody = (ErrorResponse) launchLinkResponse.getBody();
        assert responseBody != null;
        assertEquals(expectedMessage, responseBody.getMessage());
    }

    private void verifySuccessAndLaunchLinkWithDisabledBookmark(ResponseEntity<?> launchLinkResponse) {
        assertTrue(launchLinkResponse.getStatusCode().is2xxSuccessful());
        LaunchLink actualLaunchLink = (LaunchLink) launchLinkResponse.getBody();
        LaunchLink expectedLaunchLink = createLaunchLink();
        expectedLaunchLink.setLaunchLink(expectedLaunchLink.getLaunchLink() + "&clearbookmark=true");
        assertEquals(expectedLaunchLink, actualLaunchLink);
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

    private void mockLearnerRecordServiceForGetCourseRecord(ResponseEntity responseForCourseRecords) {
        when(learnerRecordService.getCourseRecordForLearner(learnerId, courseId)).thenReturn(responseForCourseRecords);
    }

    private void mockLearnerRecordServiceForCreateInProgressCourseRecordWithModuleRecord(
            CourseRecordInput courseRecordInput, CourseRecord courseRecord) {
        when(learnerRecordService.createInProgressCourseRecordWithModuleRecord(courseRecordInput)).thenReturn(courseRecord);
    }

    private void mockLearnerRecordServiceForCreateCourseRecord(
            CourseRecordInput courseRecordInput, ResponseEntity responseForCourseRecord) {
        when(learnerRecordService.createCourseRecordForLearner(courseRecordInput)).thenReturn(responseForCourseRecord);
    }

    private void mockLearnerRecordServiceForCreateModuleRecord(
            ModuleRecordInput moduleRecordInput, ResponseEntity responseForModuleRecord) {
        when(learnerRecordService.createModuleRecordForLearner(moduleRecordInput)).thenReturn(responseForModuleRecord);
    }

    private void mockLearnerRecordServiceForUpdateModuleRecord(ResponseEntity responseForModuleRecord) {
        when(learnerRecordService.updateModuleRecordForLearner(any(), any())).thenReturn(responseForModuleRecord);
    }

    private void mockLearnerRecordServiceForUpdateModuleRecordToAssignUid(ModuleRecord moduleRecord, String learnerId, String courseId) {
        ModuleRecord moduleRecord1 = createModuleRecord();
                when(learnerRecordService.updateModuleRecordToAssignUid(moduleRecord, learnerId, courseId)).thenReturn(moduleRecord1);
    }

    private void mockRusticiServiceCallGetRegistrationLaunchLink() {
        ResponseEntity responseForLaunchLink = createSuccessRusticiResponseForLaunchLink();
        when(rusticiService.getRegistrationLaunchLink(createRegistrationInput())).thenReturn(responseForLaunchLink);
    }

    private void mockRusticiServiceCallCreateRegistrationAndLaunchLink() {
        ResponseEntity responseForLaunchLink = createSuccessRusticiResponseForLaunchLink();
        when(rusticiService.createRegistrationAndLaunchLink(createRegistrationInput())).thenReturn(responseForLaunchLink);
    }

    private void mockRusticiServiceCallForRegistrationIdNotFoundError() {
        ResponseEntity rusticiResponseRegistrationIdNotFoundError = createErrorRusticiResponseForRegistrationNotFound();
        when(rusticiService.getRegistrationLaunchLink(createRegistrationInput())).thenReturn(rusticiResponseRegistrationIdNotFoundError);
    }

    private void mockRusticiServiceCallForInvalidCourseIdBadRequestError() {
        ResponseEntity createErrorRusticiResponseForInvalidCourseIdBadRequest = createErrorRusticiResponseForInvalidCourseIdBadRequest();
        when(rusticiService.createRegistrationAndLaunchLink(createRegistrationInput())).thenReturn(createErrorRusticiResponseForInvalidCourseIdBadRequest);
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

    private RegistrationInput createRegistrationInput() {
        return new RegistrationInput(uid, courseId, moduleId, learnerId, learnerFirstName, learnerLastName);
    }

    private LaunchLink createLaunchLink() {
        return new LaunchLink("https://rustici-engine/RusticiEngine/defaultui/launch.jsp?jwt=eyJ0eXAiOiJKV1");
    }

    private ResponseEntity<?> createSuccessResponseForCourseRecords() {
        return new ResponseEntity<>(convertObjectToJsonString(createCourseRecords()), HttpStatus.OK);
    }

    private ResponseEntity<?> createSuccessResponseForCourseRecordsWithEmptyModuleUid(CourseRecords courseRecords) {
        courseRecords.getCourseRecord(courseId).getModuleRecord(moduleId).setUid(null);
        return new ResponseEntity<>(convertObjectToJsonString(courseRecords), HttpStatus.OK);
    }

    private ResponseEntity<?> createSuccessResponseForCourseRecordsWithEmptyModule() {
        CourseRecords courseRecords = createCourseRecords();
        courseRecords.getCourseRecord(courseId).setModuleRecords(null);
        return new ResponseEntity<>(convertObjectToJsonString(courseRecords), HttpStatus.OK);
    }

    private ResponseEntity<?> createSuccessResponseForCourseRecordsWithEmptyCourseRecord() {
        CourseRecords courseRecords = createCourseRecords();
        courseRecords.setCourseRecords(null);
        return new ResponseEntity<>(convertObjectToJsonString(courseRecords), HttpStatus.OK);
    }

    private ResponseEntity<?> createErrorResponseForCourseRecordsWithEmptyCourseRecord() {
        CourseRecords courseRecords = createCourseRecords();
        courseRecords.setCourseRecords(null);
        return new ResponseEntity<>(convertObjectToJsonString(courseRecords), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<?> createSuccessResponseForCourseRecordWithEmptyModules() {
        CourseRecord courseRecord = createCourseRecord();
        courseRecord.setModuleRecords(null);
        return new ResponseEntity<>(convertObjectToJsonString(courseRecord), HttpStatus.OK);
    }

    private ResponseEntity<?> createSuccessResponseForModuleRecord() {
        return new ResponseEntity<>(convertObjectToJsonString(createModuleRecord()), HttpStatus.OK);
    }

    private ResponseEntity<?> createSuccessRusticiResponseForLaunchLink() {
        return new ResponseEntity<>(convertObjectToJsonString(createLaunchLink()), HttpStatus.OK);
    }

    private ResponseEntity<?> createErrorRusticiResponseForRegistrationNotFound() {
        return new ResponseEntity<>(createErrorResponseForRegistrationDoesNotExist(), HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> createErrorRusticiResponseForInvalidCourseIdBadRequest() {
        return new ResponseEntity<>(createErrorResponseForInvalidCourseId(), HttpStatus.BAD_REQUEST);
    }

    private ErrorResponse createErrorResponseForRegistrationDoesNotExist() {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("Registration ID 'registration_id' does not exist");
        return errorResponse;
    }

    private ErrorResponse createErrorResponseForInvalidCourseId() {
        String invalidCourseIdMessage = "Course ID '" + courseId + "." + moduleId + "' is invalid";
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(invalidCourseIdMessage);
        return errorResponse;
    }
}
