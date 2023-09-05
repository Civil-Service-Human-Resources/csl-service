package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cabinetoffice.csl.controller.model.ModuleResponse;
import uk.gov.cabinetoffice.csl.service.ModuleService;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;

@Slf4j
@RestController
public class ModuleCompleteController {

    private final ModuleService moduleService;
    private final IUserAuthService userAuthService;

    public ModuleCompleteController(ModuleService moduleService, IUserAuthService userAuthService) {
        this.moduleService = moduleService;
        this.userAuthService = userAuthService;
    }

    @PostMapping(path = "/courses/{courseId}/modules/{moduleId}/complete", produces = "application/json")
    public ResponseEntity<ModuleResponse> createModuleLaunchLink(@PathVariable("courseId") String courseId,
                                                                 @PathVariable("moduleId") String moduleId) {
        log.debug("courseId: {}, moduleId: {}", courseId, moduleId);
        String learnerId = userAuthService.getUsername();
        ModuleResponse response = moduleService.completeModule(learnerId, courseId, moduleId);
        return ResponseEntity.ok(response);
    }
}
