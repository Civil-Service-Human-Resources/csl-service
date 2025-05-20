package uk.gov.cabinetoffice.csl.service.learningResources.course;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.CourseWithRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Preference;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEvent;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class CourseRecordFactory {

    private final LearnerRecordService learnerRecordService;

    public CourseRecordFactory(LearnerRecordService learnerRecordService) {
        this.learnerRecordService = learnerRecordService;
    }

    public CourseRecord transformToCourseRecord(CourseWithRecord courseWithRecord) {
        LocalDateTime lastUpdated = null;
        CourseRecord courseRecord = new CourseRecord();
        courseRecord.setCourseId(courseWithRecord.getResourceId());
        courseRecord.setCourseTitle(courseWithRecord.getTitle());
        courseRecord.setUserId(courseWithRecord.getLearnerId());
        LearnerRecord learnerRecord = courseWithRecord.getRecord();
        List<ModuleRecord> moduleRecords = learnerRecordService.getModuleRecords(courseWithRecord.getModuleResourceIds());
        courseRecord.setModuleRecords(moduleRecords);
        if (moduleRecords.size() > 0) {
            courseRecord.setState(State.IN_PROGRESS);
            lastUpdated = moduleRecords.stream()
                    .map(ModuleRecord::getUpdatedAt)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
        }
        if (learnerRecord != null) {
            LearnerRecordEvent latestEvent = learnerRecord.getLatestEvent();
            if (latestEvent != null) {
                ILearnerRecordActionType actionType = latestEvent.getActionType();
                courseRecord.setLastUpdated(latestEvent.getEventTimestamp());
                if (actionType.equals(CourseRecordAction.COMPLETE_COURSE)) {
                    courseRecord.setState(State.COMPLETED);
                } else if (actionType.equals(CourseRecordAction.REMOVE_FROM_LEARNING_PLAN)) {
                    courseRecord.setState(State.ARCHIVED);
                } else if (actionType.equals(CourseRecordAction.MOVE_TO_LEARNING_PLAN)) {
                    courseRecord.setPreference(Preference.LIKED);
                } else if (actionType.equals(CourseRecordAction.REMOVE_FROM_SUGGESTIONS)) {
                    courseRecord.setPreference(Preference.DISLIKED);
                }
                if (lastUpdated != null && lastUpdated.isBefore(latestEvent.getEventTimestamp())) {
                    lastUpdated = latestEvent.getEventTimestamp();
                }
            }
        }
        courseRecord.setLastUpdated(lastUpdated);
        return courseRecord;
    }

}
