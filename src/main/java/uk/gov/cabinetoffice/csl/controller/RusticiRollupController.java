package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cabinetoffice.csl.service.ModuleRollupService;

@Slf4j
@RestController
public class RusticiRollupController {

    private final ModuleRollupService moduleRollupService;

    public RusticiRollupController(ModuleRollupService moduleRollupService) {
        this.moduleRollupService = moduleRollupService;
    }
}
