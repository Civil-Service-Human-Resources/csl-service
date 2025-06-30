package uk.gov.cabinetoffice.csl.domain.learnerrecord.ID;

import uk.gov.cabinetoffice.csl.domain.LearningResourceType;

public class CourseRecordResourceId extends TypedLearnerRecordResourceId {

    public CourseRecordResourceId(String learnerId, String resourceId) {
        super(learnerId, resourceId, LearningResourceType.COURSE);
    }
}
