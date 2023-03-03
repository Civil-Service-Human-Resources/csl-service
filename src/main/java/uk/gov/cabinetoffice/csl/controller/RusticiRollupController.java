package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cabinetoffice.csl.domain.RusticiRollupData;
import uk.gov.cabinetoffice.csl.service.RusticiModuleService;

@Slf4j
@RestController
public class RusticiRollupController {

    private final RusticiModuleService rusticiModuleService;

    public RusticiRollupController(RusticiModuleService rusticiModuleService) {
        this.rusticiModuleService = rusticiModuleService;
    }

    @PostMapping(path = "/rustici/rollup", produces = "application/json")
    public ResponseEntity<?> createModuleLaunchLink(@RequestBody RusticiRollupData rusticiRollupData) {
        if(rusticiRollupData != null
                && rusticiRollupData.getLearner() != null && rusticiRollupData.getLearner().getId() != null
                && rusticiRollupData.getCourse() != null && rusticiRollupData.getCourse().getId() != null) {
            rusticiModuleService.processRusticiRollupData(rusticiRollupData);
        } else {
            log.error("Invalid rustici rollup data: {}", rusticiRollupData);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
