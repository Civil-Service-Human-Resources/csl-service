package uk.gov.cabinetoffice.csl.service.learningResources.course;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.CourseWithRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;

import java.util.List;

@Service
public class CourseRecordFactory {

    private final LearnerRecordService learnerRecordService;

    public CourseRecordFactory(LearnerRecordService learnerRecordService) {
        this.learnerRecordService = learnerRecordService;
    }

    public CourseRecord transformToCourseRecord(CourseWithRecord courseWithRecord) {
        CourseRecord courseRecord = new CourseRecord();
        courseRecord.setCourseId(courseWithRecord.getResourceId());
        courseRecord.setCourseTitle(courseWithRecord.getTitle());
        ILearnerRecord learnerRecord = courseWithRecord.getRecord();
        List<ModuleRecord> moduleRecords = learnerRecordService.getModuleRecords(courseWithRecord.getModuleResourceIds());
        courseRecord.setModuleRecords(moduleRecords);
        if (moduleRecords.size() > 0) {
            courseRecord.setState(State.IN_PROGRESS);
        }
        if (learnerRecord != null) {
            ILearnerRecordActionType actionType = learnerRecord.getLatestEvent().getActionType();
            if (actionType.equals(CourseRecordAction.COMPLETE_COURSE)) {
                courseRecord.setState(State.COMPLETED);
            } else if (actionType.equals(CourseRecordAction.REMOVE_FROM_LEARNING_PLAN)) {
                courseRecord.setState(State.ARCHIVED);
            } else if (actionType.equals(CourseRecordAction.ADD_TO_LEARNING_PLAN)) {
                courseRecord.setPreference(Preference.LIKED);
            } else if (actionType.equals(CourseRecordAction.REMOVE_FROM_SUGGESTIONS)) {
                courseRecord.setPreference(Preference.DISLIKED);
            }
        }
        return courseRecord;
    }

}
