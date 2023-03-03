package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cabinetoffice.csl.domain.RusticiRollupData;
import uk.gov.cabinetoffice.csl.service.ModuleRollupService;

@Slf4j
@RestController
public class RusticiRollupController {

    private final ModuleRollupService moduleRollupService;

    public RusticiRollupController(ModuleRollupService moduleRollupService) {
        this.moduleRollupService = moduleRollupService;
    }

    @PostMapping(path = "/rustici/rollup", produces = "application/json")
    public ResponseEntity<?> createModuleLaunchLink(@RequestBody RusticiRollupData rusticiRollupData) {
        if(rusticiRollupData != null
                && rusticiRollupData.getLearner() != null && rusticiRollupData.getLearner().getId() != null
                && rusticiRollupData.getCourse() != null && rusticiRollupData.getCourse().getId() != null) {
            moduleRollupService.processRusticiRollupData(rusticiRollupData);
        } else {
            log.error("Invalid rustici rollup data: {}", rusticiRollupData);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
