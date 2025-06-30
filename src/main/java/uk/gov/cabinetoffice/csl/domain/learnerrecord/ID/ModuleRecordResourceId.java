package uk.gov.cabinetoffice.csl.domain.learnerrecord.ID;

import uk.gov.cabinetoffice.csl.domain.LearningResourceType;

public class ModuleRecordResourceId extends TypedLearnerRecordResourceId {

    public ModuleRecordResourceId(String learnerId, String resourceId) {
        super(learnerId, resourceId, LearningResourceType.MODULE);
    }
}
