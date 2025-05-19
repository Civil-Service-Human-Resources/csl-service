package uk.gov.cabinetoffice.csl.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.model.ModuleResponse;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.error.GenericServerException;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.UserDetailsDto;
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
                                         @PathVariable("moduleId") String moduleId,
                                         @RequestBody @Valid UserDetailsDto userDetailsDto) {
        log.debug("resourceId: {}, moduleId: {}", courseId, moduleId);
        User user = User.fromUserDetails(userAuthService.getUsername(), userDetailsDto);
        return moduleService.completeModule(user, courseId, moduleId);
    }

    @PostMapping(path = "/courses/{courseId}/modules/{moduleId}/launch", produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public LaunchLink createModuleLaunchLink(@PathVariable("courseId") String courseId,
                                             @PathVariable("moduleId") String moduleId,
                                             @RequestBody @Valid UserDetailsDto userDetailsDto) {
        log.debug("resourceId: {}, moduleId: {}", courseId, moduleId);
        User user = User.fromUserDetails(userAuthService.getUsername(), userDetailsDto);
        LaunchLink launchLink = moduleService.launchModule(user, courseId, moduleId, userDetailsDto);
        if (launchLink == null) {
            throw new GenericServerException("Unable to retrieve module launch link for the learnerId: " + user.getId() + ", resourceId: "
                    + courseId + " and moduleId: " + moduleId);
        } else {
            return launchLink;
        }
    }
}
