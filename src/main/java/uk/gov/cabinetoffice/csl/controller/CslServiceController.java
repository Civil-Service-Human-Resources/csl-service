package uk.gov.cabinetoffice.csl.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.domain.CourseRecordInput;
import uk.gov.cabinetoffice.csl.service.ModuleLaunchService;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.getLearnerIdFromAuth;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.returnError;

@Slf4j
@RestController
@RequestMapping(path = "/csl")
public class CslServiceController {

    private final ModuleLaunchService moduleLaunchService;

    public CslServiceController(ModuleLaunchService moduleLaunchService) {
        this.moduleLaunchService = moduleLaunchService;
    }

    @PostMapping(path = "/courses/{courseId}/modules/{moduleId}/launch", produces = "application/json")
    public ResponseEntity<?> createModuleLaunchLink(@PathVariable("courseId") String courseId,
                                                    @PathVariable("moduleId") String moduleId,
                                                    @Valid @RequestBody CourseRecordInput courseRecordInput,
                                                    Authentication authentication) {
        log.debug("courseId: {}, moduleId: {}", courseId, moduleId);
        String learnerId = getLearnerIdFromAuth(authentication);
        if(StringUtils.isBlank(learnerId)) {
            return returnError(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Learner Id is missing from authentication token","/course-record");
        }
        courseRecordInput.setUserId(learnerId);
        courseRecordInput.setCourseId(courseId);

        courseRecordInput.getModuleRecords().get(0).setUserId(learnerId);
        courseRecordInput.getModuleRecords().get(0).setModuleId(moduleId);

        return moduleLaunchService.createLaunchLink(courseRecordInput);
    }
}
