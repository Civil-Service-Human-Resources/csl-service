package uk.gov.cabinetoffice.csl.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.cabinetoffice.csl.domain.CourseRecordInput;
import uk.gov.cabinetoffice.csl.domain.ModuleLaunchLinkInput;
import uk.gov.cabinetoffice.csl.domain.ModuleRecordInput;

import java.util.ArrayList;

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

    @Test
    public void createLaunchLinkShouldReturnErrorWhenGetCourseRecordForLearnerReturnsError() {
        ResponseEntity errorResponse = createErrorResponse(
                "Unable to retrieve course record for the learnerId: " + learnerId + ", courseId: " + courseId
                + ", modules/" +  moduleId, "/course_records?userId=" + learnerId + "&courseId=" + courseId);
        when(learnerRecordService.getCourseRecordForLearner(learnerId, courseId)).thenReturn(errorResponse);
        ResponseEntity<?> launchLinkResponse = moduleLaunchService.createLaunchLink(createModuleLaunchLinkInput(learnerId, courseId, moduleId));
        assertTrue(launchLinkResponse.getStatusCode().is5xxServerError());
    }

    private ResponseEntity<?> createErrorResponse(String message, String path) {
        return returnError(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                message, path, null);
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
        String courseTitle = "courseTitle";
        Boolean isRequired = true;
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
}
