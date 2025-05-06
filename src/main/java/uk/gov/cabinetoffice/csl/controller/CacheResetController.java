package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordId;
import uk.gov.cabinetoffice.csl.service.IdentityService;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;

@Slf4j
@RestController
@RequestMapping("/reset-cache")
public class CacheResetController {

    private final LearnerRecordService learnerRecordService;
    private final IdentityService identityService;

    private final LearningCatalogueService learningCatalogueService;

    public CacheResetController(LearnerRecordService learnerRecordService,
                                IdentityService identityService, LearningCatalogueService learningCatalogueService) {
        this.learnerRecordService = learnerRecordService;
        this.identityService = identityService;
        this.learningCatalogueService = learningCatalogueService;
    }

    @GetMapping(path = "/service-token", produces = "application/json")
    public ResponseEntity<?> removeServiceTokenFromCache() {
        identityService.removeServiceTokenFromCache();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/course/{courseId}", produces = "application/json")
    public ResponseEntity<?> removeCoursesFromCache(@PathVariable String courseId) {
        learningCatalogueService.removeCourseFromCache(courseId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/learner/{learnerId}/course_record/{courseId}")
    public ResponseEntity<?> removeCourseRecordFromCache(@PathVariable String learnerId,
                                                         @PathVariable String courseId) {
        learnerRecordService.bustCourseRecordCache(new CourseRecordId(learnerId, courseId));
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
