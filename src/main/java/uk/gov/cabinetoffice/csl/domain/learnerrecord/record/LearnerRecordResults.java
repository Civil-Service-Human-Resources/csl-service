package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.Data;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;

import java.util.ArrayList;
import java.util.List;

@Data
public class LearnerRecordResults {
    private final List<ModuleRecord> newModuleRecords = new ArrayList<>();
    private final List<ModuleRecord> moduleRecordUpdates = new ArrayList<>();
    private final List<LearnerRecordData> learnerRecordUpdates = new ArrayList<>();

    public void add(LearnerRecordResults result) {
        this.newModuleRecords.addAll(result.getNewModuleRecords());
        this.moduleRecordUpdates.addAll(result.getModuleRecordUpdates());
        this.learnerRecordUpdates.addAll(result.getLearnerRecordUpdates());
    }

    public boolean isEmpty() {
        return moduleRecordUpdates.isEmpty() && learnerRecordUpdates.isEmpty();
    }
}
