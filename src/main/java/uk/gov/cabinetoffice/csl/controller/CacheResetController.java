package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.service.IdentityService;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.civilservantregistry.CivilServantRegistryService;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;

@Slf4j
@RestController
@RequestMapping("/reset-cache")
public class CacheResetController {

    private final LearnerRecordService learnerRecordService;
    private final IdentityService identityService;
    private final LearningCatalogueService learningCatalogueService;
    private final CivilServantRegistryService civilServantRegistryService;

    public CacheResetController(LearnerRecordService learnerRecordService,
                                IdentityService identityService, LearningCatalogueService learningCatalogueService,
                                CivilServantRegistryService civilServantRegistryService) {
        this.learnerRecordService = learnerRecordService;
        this.identityService = identityService;
        this.learningCatalogueService = learningCatalogueService;
        this.civilServantRegistryService = civilServantRegistryService;
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

    @GetMapping(path = "/learner/{learnerId}/module_record/{moduleRecordId}")
    public ResponseEntity<?> removeModuleRecordFromCache(@PathVariable String learnerId,
                                                         @PathVariable String moduleRecordId) {
        learnerRecordService.bustModuleRecordCache(new ModuleRecordResourceId(learnerId, moduleRecordId));
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/learner/{learnerId}/learner_record/{resourceId}")
    public ResponseEntity<?> removeLearnerRecordFromCache(@PathVariable String learnerId,
                                                          @PathVariable String resourceId) {
        learnerRecordService.bustLearnerRecordCache(learnerId, resourceId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/organisations", produces = "application/json")
    public ResponseEntity<?> removeOrganisationsFromCache() {
        civilServantRegistryService.removeOrganisationsFromCache();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
