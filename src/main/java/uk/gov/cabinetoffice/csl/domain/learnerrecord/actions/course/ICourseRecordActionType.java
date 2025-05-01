package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course;

import uk.gov.cabinetoffice.csl.domain.LearningResourceType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;

public interface ICourseRecordActionType extends ILearnerRecordActionType {

    @Override
    default LearningResourceType getRecordType() {
        return LearningResourceType.COURSE;
    }

}
