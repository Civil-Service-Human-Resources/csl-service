package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.domain.CourseRecordInput;
import uk.gov.cabinetoffice.csl.domain.ModuleLaunchLinkInput;
import uk.gov.cabinetoffice.csl.domain.ModuleRecordInput;
import uk.gov.cabinetoffice.csl.service.ModuleLaunchService;

import java.util.ArrayList;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.getLearnerIdFromAuth;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.returnError;

@Slf4j
@RestController
public class CslServiceController {

    private final ModuleLaunchService moduleLaunchService;

    public CslServiceController(ModuleLaunchService moduleLaunchService) {
        this.moduleLaunchService = moduleLaunchService;
    }

    @PostMapping(path = "/courses/{courseId}/modules/{moduleId}/launch", produces = "application/json")
    public ResponseEntity<?> createModuleLaunchLink(@PathVariable("courseId") String courseId,
                                                    @PathVariable("moduleId") String moduleId,
                                                    @RequestBody ModuleLaunchLinkInput moduleLaunchLinkInput,
                                                    Authentication authentication) {
        log.debug("courseId: {}, moduleId: {}", courseId, moduleId);

        String learnerId = getLearnerIdFromAuth(authentication);
        if(StringUtils.isBlank(learnerId)) {
            return returnError(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Learner Id is missing from authentication token",
                    "/courses/" + courseId + "/modules/" +  moduleId + "/launch", null);
        }

        CourseRecordInput courseRecordInput = moduleLaunchLinkInput.getCourseRecordInput();

        if(courseRecordInput != null && courseRecordInput.getModuleRecords() != null
                && courseRecordInput.getModuleRecords().size() != 1) {
            return returnError(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Either 0 or more than 1 modules are present in the request body.",
                    "/courses/" + courseId + "/modules/" +  moduleId + "/launch", null);
        }

        courseRecordInput = setupCourseRecordInput(courseRecordInput, learnerId, courseId, moduleId);
        moduleLaunchLinkInput.setCourseRecordInput(courseRecordInput);

        return moduleLaunchService.createLaunchLink(moduleLaunchLinkInput);
    }

    private CourseRecordInput setupCourseRecordInput(CourseRecordInput courseRecordInput,
                                                     String learnerId, String courseId, String moduleId) {
        if(courseRecordInput == null) {
            courseRecordInput = new CourseRecordInput();
        }

        courseRecordInput.setUserId(learnerId);
        courseRecordInput.setCourseId(courseId);

        if(courseRecordInput.getModuleRecords() == null || courseRecordInput.getModuleRecords().isEmpty()) {
            courseRecordInput.setModuleRecords(new ArrayList<>());
            courseRecordInput.getModuleRecords().add(new ModuleRecordInput());
        }

        courseRecordInput.getModuleRecords().get(0).setUserId(learnerId);
        courseRecordInput.getModuleRecords().get(0).setCourseId(courseId);
        courseRecordInput.getModuleRecords().get(0).setModuleId(moduleId);

        return courseRecordInput;
    }
}
