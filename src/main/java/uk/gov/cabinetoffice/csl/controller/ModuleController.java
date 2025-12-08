package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.model.ModuleResponse;
import uk.gov.cabinetoffice.csl.domain.error.GenericServerException;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.service.ModuleService;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;

@Slf4j
@RestController
public class ModuleController {

    private final ModuleService moduleService;
    private final IUserAuthService userAuthService;

    public ModuleController(ModuleService moduleService, IUserAuthService userAuthService) {
        this.moduleService = moduleService;
        this.userAuthService = userAuthService;
    }

    @PostMapping(path = "/courses/{courseId}/modules/{moduleId}/complete", produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ModuleResponse completeModule(@PathVariable("courseId") String courseId,
                                         @PathVariable("moduleId") String moduleId) {
        log.debug("resourceId: {}, moduleId: {}", courseId, moduleId);
        return moduleService.completeModule(userAuthService.getUsername(), courseId, moduleId);
    }

    @PostMapping(path = "/courses/{courseId}/modules/{moduleId}/launch", produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public LaunchLink createModuleLaunchLink(@PathVariable("courseId") String courseId,
                                             @PathVariable("moduleId") String moduleId) {
        log.debug("resourceId: {}, moduleId: {}", courseId, moduleId);
        LaunchLink launchLink = moduleService.launchModule(userAuthService.getUsername(), courseId, moduleId);
        if (launchLink == null) {
            throw new GenericServerException("Unable to retrieve module launch link for the learnerId: " + userAuthService.getUsername() + ", resourceId: "
                    + courseId + " and moduleId: " + moduleId);
        } else {
            return launchLink;
        }
    }
}
