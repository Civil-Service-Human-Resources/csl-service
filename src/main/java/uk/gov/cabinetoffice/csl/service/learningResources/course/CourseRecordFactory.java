package uk.gov.cabinetoffice.csl.service.learningResources.course;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.CourseWithRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Preference;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEvent;
import uk.gov.cabinetoffice.csl.service.LearnerRecordDataUtils;
import uk.gov.cabinetoffice.csl.service.learning.ModuleRecordCollection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CourseRecordFactory {

    private final LearnerRecordDataUtils learnerRecordDataUtils;

    public CourseRecordFactory(LearnerRecordDataUtils learnerRecordDataUtils) {
        this.learnerRecordDataUtils = learnerRecordDataUtils;
    }

    public List<CourseRecord> transformToCourseRecords(List<CourseWithRecord> coursesWithRecord) {
        List<String> courseIds = new ArrayList<>();
        List<ModuleRecordResourceId> moduleRecordResourceIds = new ArrayList<>();
        coursesWithRecord.forEach(courseWithRecord -> {
            courseIds.add(courseWithRecord.getCourseId());
            moduleRecordResourceIds.addAll(courseWithRecord.getModuleResourceIds());
        });
        Map<String, ModuleRecordCollection> courseToModuleRecords = learnerRecordDataUtils.getModuleRecordsForCourses(courseIds, moduleRecordResourceIds);
        return coursesWithRecord.stream().map(c -> {
            ModuleRecordCollection moduleRecordCollection = courseToModuleRecords.get(c.getCourseId());
            return transformToCourseRecord(c, moduleRecordCollection, moduleRecordCollection.getLatestUpdatedDate());
        }).toList();
    }

    public CourseRecord transformToCourseRecord(CourseWithRecord courseWithRecord, List<ModuleRecord> moduleRecords, LocalDateTime latestModuleUpdatedDate) {
        LocalDateTime lastUpdated = null;
        LearnerRecordEvent latestEvent = courseWithRecord.getLatestEvent();
        CourseRecord courseRecord = new CourseRecord();
        courseRecord.setCourseId(courseWithRecord.getResourceId());
        courseRecord.setCourseTitle(courseWithRecord.getCourseTitle());
        courseRecord.setUserId(courseWithRecord.getLearnerId());
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
            lastUpdated = latestEvent.getEventTimestamp();
        }
        courseRecord.setModuleRecords(moduleRecords);
        if (latestModuleUpdatedDate != LocalDateTime.MIN && lastUpdated != null && latestModuleUpdatedDate.isAfter(lastUpdated)) {
            lastUpdated = latestModuleUpdatedDate;
            if (!courseRecord.equalsAnyState(State.COMPLETED)) {
                courseRecord.setState(State.IN_PROGRESS);
            }
        }
        courseRecord.setLastUpdated(lastUpdated);
        return courseRecord;
    }

}
