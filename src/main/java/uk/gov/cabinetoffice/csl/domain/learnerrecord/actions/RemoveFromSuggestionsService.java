package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.domain.error.RecordAlreadyExistsException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordStatus;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Preference;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;

@Component
public class RemoveFromSuggestionsService extends CourseActionService {

    public RemoveFromSuggestionsService(LearnerRecordService learnerRecordService) {
        super(learnerRecordService);
    }

    @Override
    public CourseRecord processNewCourseRecord(String learnerId, String courseId) {
        CourseRecordStatus status = CourseRecordStatus.builder().preference(Preference.DISLIKED.name()).build();
        return learnerRecordService.createCourseRecord(learnerId, courseId, status);
    }

    @Override
    public CourseRecord processExistingCourseRecord(CourseRecord courseRecord) {
        throw new RecordAlreadyExistsException(String.format("Course record with ID '%s' already exists for user '%s'",
                courseRecord.getCourseId(), courseRecord.getUserId()));
    }

    @Override
    public CourseRecordAction getType() {
        return CourseRecordAction.REMOVE_FROM_SUGGESTIONS;
    }

}
