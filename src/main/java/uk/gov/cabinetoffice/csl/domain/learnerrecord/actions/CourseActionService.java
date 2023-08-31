package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;

public abstract class CourseActionService {

    protected final LearnerRecordService learnerRecordService;

    public CourseActionService(LearnerRecordService learnerRecordService) {
        this.learnerRecordService = learnerRecordService;
    }

    public abstract CourseRecord processNewCourseRecord(String learnerId, String courseId);

    public abstract CourseRecord processExistingCourseRecord(CourseRecord courseRecord);

    public abstract CourseRecordAction getType();
}
