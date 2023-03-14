package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cabinetoffice.csl.service.IdentityService;
import uk.gov.cabinetoffice.csl.service.LearningCatalogueService;

@Slf4j
@RestController
@RequestMapping("/reset-cache")
public class CacheResetController {

    private final IdentityService identityService;

    private final LearningCatalogueService learningCatalogueService;

    public CacheResetController(IdentityService identityService, LearningCatalogueService learningCatalogueService) {
        this.identityService = identityService;
        this.learningCatalogueService = learningCatalogueService;
    }

    @GetMapping(path = "/service-token", produces = "application/json")
    public ResponseEntity<?> removeServiceTokenFromCache() {
        identityService.removeServiceTokenFromCache();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/all-courses", produces = "application/json")
    public ResponseEntity<?> removeAllCoursesFromCache() {
        learningCatalogueService.removeAllCoursesFromCache();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/course/{courseId}", produces = "application/json")
    public ResponseEntity<?> removeCoursesFromCache(@PathVariable String courseId) {
        learningCatalogueService.removeCourseFromCache(courseId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}