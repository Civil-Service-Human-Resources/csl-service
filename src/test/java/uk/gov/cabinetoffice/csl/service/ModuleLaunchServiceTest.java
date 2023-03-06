package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.cabinetoffice.csl.domain.*;
import uk.gov.cabinetoffice.csl.util.CslTestUtil;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.convertObjectToJsonString;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.createInternalServerErrorResponse;

@SpringBootTest
public class ModuleLaunchServiceTest {

    @Mock
    private LearnerRecordService learnerRecordService;

    @Mock
    private RusticiService rusticiService;

    @InjectMocks
    private ModuleLaunchService moduleLaunchService;

    private CslTestUtil cslTestUtil;

    private final String learnerId = "learnerId";
    private final String courseId = "courseId";

    private final String moduleId = "moduleId";
    private final String uid = randomUUID().toString();
    private final String learnerFirstName = "learnerFirstName";
    private final String learnerLastName = "";

    private final String[] disabledBookmarkingModuleIDs = {moduleId, "moduleId1"};

    @BeforeEach
    public void setup() {
        moduleLaunchService = new ModuleLaunchService(learnerRecordService, rusticiService, disabledBookmarkingModuleIDs);
        cslTestUtil = new CslTestUtil(learnerRecordService, learnerId, courseId, moduleId, uid);
    }

    @Test
    public void createLaunchLinkShouldCreateCourseAndModuleAndThenReturnLaunchLinkWithDisabledBookmark() {
        cslTestUtil.mockLearnerRecordServiceGetAndUpdateCalls(
                cslTestUtil.createSuccessResponseForCourseRecordsWithEmptyCourseRecord(),
                cslTestUtil.createSuccessResponseForModuleRecord());
        cslTestUtil.mockLearnerRecordServiceForCreateInProgressCourseRecordWithModuleRecord(
                cslTestUtil.createCourseRecordInput(learnerId, courseId, moduleId),
                cslTestUtil.createCourseRecord());
        cslTestUtil.mockLearnerRecordServiceCreateCalls(
                cslTestUtil.createCourseRecordInput(learnerId, courseId, moduleId),
                cslTestUtil.createSuccessResponseForCourseRecordWithEmptyModules(),
                cslTestUtil.createModuleRecordInput(learnerId, courseId, moduleId),
                cslTestUtil.createSuccessResponseForModuleRecord());
        mockRusticiServiceCallGetRegistrationLaunchLink();
        verifySuccessAndLaunchLinkWithDisabledBookmark(invokeService());
    }

    @Test
    public void createLaunchLinkShouldUpdateModuleUidThenReturnLaunchLinkWithDisabledBookmark() {
        CourseRecords courseRecords = cslTestUtil.createCourseRecords();
        cslTestUtil.mockLearnerRecordServiceGetAndUpdateCalls(
                cslTestUtil.createSuccessResponseForCourseRecordsWithEmptyModuleUid(courseRecords),
                cslTestUtil.createSuccessResponseForModuleRecord());
        ModuleRecord moduleRecord = courseRecords.getCourseRecord(courseId).getModuleRecord(moduleId);
        cslTestUtil.mockLearnerRecordServiceForUpdateModuleRecordToAssignUid(moduleRecord, learnerId, courseId);
        mockRusticiServiceCallGetRegistrationLaunchLink();
        verifySuccessAndLaunchLinkWithDisabledBookmark(invokeService());
    }

    @Test
    public void createLaunchLinkShouldCreateRegistrationWhenRegistrationIdNotFoundThenReturnLaunchLinkWithDisabledBookmark() {
        CourseRecords courseRecords = cslTestUtil.createCourseRecords();
        cslTestUtil.mockLearnerRecordServiceGetAndUpdateCalls(
                cslTestUtil.createSuccessResponseForCourseRecordsWithEmptyModuleUid(courseRecords),
                cslTestUtil.createSuccessResponseForModuleRecord());
        ModuleRecord moduleRecord = courseRecords.getCourseRecord(courseId).getModuleRecord(moduleId);
        cslTestUtil.mockLearnerRecordServiceForUpdateModuleRecordToAssignUid(moduleRecord, learnerId, courseId);
        mockRusticiServiceCallForRegistrationIdNotFoundError();
        mockRusticiServiceCallCreateRegistrationAndLaunchLink();
        verifySuccessAndLaunchLinkWithDisabledBookmark(invokeService());
    }

    @Test
    public void createLaunchLinkShouldReturnErrorWhenGetCourseRecordReturnsError() {
        cslTestUtil.mockLearnerRecordServiceForGetCourseRecord(
                cslTestUtil.createErrorResponseForCourseRecordsWithEmptyCourseRecord());
        verify5xxError(invokeService());
    }

    @Test
    public void createLaunchLinkShouldReturnErrorWhenCreateCourseRecordReturnsError() {
        cslTestUtil.mockLearnerRecordServiceForGetCourseRecord(
                cslTestUtil.createSuccessResponseForCourseRecordsWithEmptyCourseRecord());
        cslTestUtil.mockLearnerRecordServiceForCreateCourseRecord(
                cslTestUtil.createCourseRecordInput(learnerId, courseId, moduleId),
                cslTestUtil.createErrorResponseForCourseRecordsWithEmptyCourseRecord());
        verify5xxError(invokeService());
    }

    @Test
    public void createLaunchLinkShouldReturnErrorWhenCreateModuleRecordReturnsError() {
        cslTestUtil.mockLearnerRecordServiceForGetCourseRecord(
                cslTestUtil.createSuccessResponseForCourseRecordsWithEmptyModule());
        cslTestUtil.mockLearnerRecordServiceForCreateModuleRecord(
                cslTestUtil.createModuleRecordInput(learnerId, courseId, moduleId),
                createInternalServerErrorResponse());
        verify5xxError(invokeService());
    }

    @Test
    public void createLaunchLinkShouldReturnErrorWhenUpdateModuleRecordUidReturnsError() {
        CourseRecords courseRecords = cslTestUtil.createCourseRecords();
        cslTestUtil.mockLearnerRecordServiceForGetCourseRecord(
                cslTestUtil.createSuccessResponseForCourseRecordsWithEmptyModuleUid(courseRecords));
        cslTestUtil.mockLearnerRecordServiceForUpdateModuleRecord(createInternalServerErrorResponse());
        verify5xxError(invokeService());
    }

    @Test
    public void createLaunchLinkShouldReturnErrorWhenRegistrationIdNotFoundAndThenNotAbleToCreateRegistrationInRustici() {
        cslTestUtil.mockLearnerRecordServiceGetAndUpdateCalls(
                cslTestUtil.createSuccessResponseForCourseRecords(),
                cslTestUtil.createSuccessResponseForModuleRecord());
        mockRusticiServiceCallForRegistrationIdNotFoundError();
        mockRusticiServiceCallForInvalidCourseIdBadRequestError();
        String invalidCourseIdMessage = "Course ID '" + courseId + "." + moduleId + "' is invalid";
        verify4xxError(invalidCourseIdMessage, invokeService());
    }

    private ResponseEntity<?> invokeService() {
        return moduleLaunchService.createLaunchLink(createModuleLaunchLinkInput(learnerId, courseId, moduleId));
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
        ResponseEntity createErrorRusticiResponseForInvalidCourseIdBadRequest =
                cslTestUtil.createErrorRusticiResponseForInvalidCourseIdBadRequest();
        when(rusticiService.createRegistrationAndLaunchLink(createRegistrationInput()))
                .thenReturn(createErrorRusticiResponseForInvalidCourseIdBadRequest);
    }

    private ModuleLaunchLinkInput createModuleLaunchLinkInput(String learnerId, String courseId, String moduleId) {
        ModuleLaunchLinkInput moduleLaunchLinkInput = new ModuleLaunchLinkInput();
        moduleLaunchLinkInput.setLearnerFirstName(learnerFirstName);
        moduleLaunchLinkInput.setLearnerLastName(learnerLastName);
        moduleLaunchLinkInput.setCourseRecordInput(cslTestUtil.createCourseRecordInput(learnerId, courseId, moduleId));
        return moduleLaunchLinkInput;
    }

    private RegistrationInput createRegistrationInput() {
        return new RegistrationInput(uid, courseId, moduleId, learnerId, learnerFirstName, learnerLastName);
    }

    private LaunchLink createLaunchLink() {
        return new LaunchLink("https://rustici-engine/RusticiEngine/defaultui/launch.jsp?jwt=eyJ0eXAiOiJKV1");
    }


    private ResponseEntity<?> createSuccessRusticiResponseForLaunchLink() {
        return new ResponseEntity<>(convertObjectToJsonString(createLaunchLink()), HttpStatus.OK);
    }

    private ResponseEntity<?> createErrorRusticiResponseForRegistrationNotFound() {
        return new ResponseEntity<>(createErrorResponseForRegistrationDoesNotExist(), HttpStatus.NOT_FOUND);
    }

    private ErrorResponse createErrorResponseForRegistrationDoesNotExist() {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("Registration ID 'registration_id' does not exist");
        return errorResponse;
    }
}
