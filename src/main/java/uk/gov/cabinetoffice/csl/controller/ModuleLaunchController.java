package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cabinetoffice.csl.domain.error.GenericServerException;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.ModuleLaunchLinkInput;
import uk.gov.cabinetoffice.csl.service.ModuleLaunchService;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;

@Slf4j
@RestController
public class ModuleLaunchController {

    private final ModuleLaunchService moduleLaunchService;
    private final IUserAuthService userAuthService;

    public ModuleLaunchController(ModuleLaunchService moduleLaunchService, IUserAuthService userAuthService) {
        this.moduleLaunchService = moduleLaunchService;
        this.userAuthService = userAuthService;
    }

    @PostMapping(path = "/courses/{courseId}/modules/{moduleId}/launch", produces = "application/json")
    public ResponseEntity<?> createModuleLaunchLink(@PathVariable("courseId") String courseId,
                                                    @PathVariable("moduleId") String moduleId,
                                                    @RequestBody ModuleLaunchLinkInput moduleLaunchLinkInput) {
        log.debug("courseId: {}, moduleId: {}", courseId, moduleId);
        String learnerId = userAuthService.getUsername();
        LaunchLink launchLink = moduleLaunchService.createLaunchLink(learnerId, courseId, moduleId, moduleLaunchLinkInput);
        if (launchLink == null) {
            throw new GenericServerException("Unable to retrieve module launch link for the learnerId: " + learnerId + ", courseId: "
                    + courseId + " and moduleId: " + moduleId);
        } else {
            return ResponseEntity.ok(launchLink);
        }
    }

}
