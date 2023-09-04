package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.error.RecordNotFoundException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.rustici.CSLRusticiProps;
import uk.gov.cabinetoffice.csl.domain.rustici.RusticiRollupData;

import java.util.List;

@Slf4j
@Service
public class ModuleRollupService {

    private final LearnerRecordService learnerRecordService;
    private final RusticiCSLDataService rusticiCSLDataService;

    public ModuleRollupService(LearnerRecordService learnerRecordService,
                               RusticiCSLDataService rusticiCSLDataService) {
        this.learnerRecordService = learnerRecordService;
        this.rusticiCSLDataService = rusticiCSLDataService;
    }

    public void processRusticiRollupData(RusticiRollupData rusticiRollupData) {
        log.info("rusticiRollupData: {}", rusticiRollupData);
        CSLRusticiProps properties = rusticiCSLDataService.getCSLDataFromRollUpData(rusticiRollupData);
        List<PatchOp> patches = properties.getModuleRecordPatches();
        if (!patches.isEmpty()) {
            CourseRecord courseRecord = learnerRecordService.getCourseRecord(properties.getLearnerId(), properties.getCourseId());
            if (courseRecord != null) {
                ModuleRecord moduleRecord = courseRecord.getModuleRecord(properties.getModuleId());
                if (moduleRecord != null) {
                    moduleRecord = learnerRecordService.updateModuleRecord(moduleRecord.getId(), patches);
                    courseRecord.updateModuleRecords(moduleRecord);
                    if (learnerRecordService.isCourseCompleted(courseRecord)) {
                        List<PatchOp> courseRecordPatches = List.of(PatchOp.replacePatch("state", State.COMPLETED.name()));
                        courseRecord = learnerRecordService.updateCourseRecord(properties.getLearnerId(), properties.getCourseId(), courseRecordPatches);
                    }
                    log.debug("moduleRecord after processing rollup data: {}", moduleRecord);
                } else {
                    throw new RecordNotFoundException(String.format("Unable to process the rustici rollup data: %s. Module record with id '%s' was null", rusticiRollupData, properties.getModuleId()));
                }
            } else {
                throw new RecordNotFoundException(String.format("Unable to process the rustici rollup data: %s. Course record with id '%s' was null", rusticiRollupData, properties.getCourseId()));
            }
            log.debug("courseRecord after processing rollup data: {}", courseRecord);
        }
    }

}
