package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ModuleRollupService {

    private final LearnerRecordService learnerRecordService;

    private final RusticiService rusticiService;

    private final IdentityService identityService;

    public ModuleRollupService(LearnerRecordService learnerRecordService, RusticiService rusticiService,
                               IdentityService identityService) {
        this.learnerRecordService = learnerRecordService;
        this.rusticiService = rusticiService;
        this.identityService = identityService;
    }
}
