package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import uk.gov.cabinetoffice.csl.domain.LearningResourceType;

public class ModuleRecordResourceId extends LearnerRecordResourceId {

    public ModuleRecordResourceId(String learnerId, String resourceId) {
        super(LearningResourceType.MODULE, learnerId, resourceId);
    }

}
