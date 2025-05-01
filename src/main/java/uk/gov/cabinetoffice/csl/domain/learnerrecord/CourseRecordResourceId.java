package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import uk.gov.cabinetoffice.csl.domain.LearningResourceType;

public class CourseRecordResourceId extends LearnerRecordResourceId {

    public CourseRecordResourceId(String learnerId, String resourceId) {
        super(LearningResourceType.COURSE, learnerId, resourceId);
    }

}
