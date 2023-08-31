package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import uk.gov.cabinetoffice.csl.domain.error.RecordAlreadyExistsException;
import uk.gov.cabinetoffice.csl.domain.error.RecordNotFoundException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;

public abstract class CourseActionService {

    protected final LearnerRecordService learnerRecordService;

    public CourseActionService(LearnerRecordService learnerRecordService) {
        this.learnerRecordService = learnerRecordService;
    }

    public CourseRecord createCourseRecord(String learnerId, String courseId) {
        throw new RecordNotFoundException(String.format("Course record with ID '%s' does not exist for user '%s'",
                courseId, learnerId));
    }

    public CourseRecord updateCourseRecord(CourseRecord courseRecord) {
        throw new RecordAlreadyExistsException(String.format("Course record with ID '%s' already exists for user '%s'",
                courseRecord.getCourseId(), courseRecord.getUserId()));
    }

    public abstract CourseRecordAction getType();
}
