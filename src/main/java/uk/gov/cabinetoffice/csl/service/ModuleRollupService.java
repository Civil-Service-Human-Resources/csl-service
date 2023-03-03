package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.RusticiRollupData;

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

    public void processRusticiRollupData(RusticiRollupData rusticiRollupData) {
        log.debug("rusticiRollupData: {}", rusticiRollupData);
        String courseIdDotModuleId = rusticiRollupData.getCourse().getId();
        if(!courseIdDotModuleId.contains(".")) {
            log.error("Invalid rustici rollup data. \".\" is missing from course.id: {}", rusticiRollupData);
            return;
        }
        String[] courseIdDotModuleIdParts = courseIdDotModuleId.split("\\.");
        String courseId = courseIdDotModuleIdParts[0];
        String moduleId = courseIdDotModuleIdParts[1];
        String learnerId = rusticiRollupData.getLearner().getId();
        log.debug("Processing Rustici rollup data for the learnerId: {}, courseId: {} and module Id: {}",
                learnerId, courseId, moduleId);
    }
}
