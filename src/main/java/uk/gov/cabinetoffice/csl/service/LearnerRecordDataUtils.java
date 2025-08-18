package uk.gov.cabinetoffice.csl.service;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.LearningResourceType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEvent;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEventQuery;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordQuery;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction.COMPLETE_COURSE;

@Service
public class LearnerRecordDataUtils {

    private final LearnerRecordService learnerRecordService;

    public LearnerRecordDataUtils(LearnerRecordService learnerRecordService) {
        this.learnerRecordService = learnerRecordService;
    }

    public List<LearnerRecord> getNonCompleteCourseRecords(String userId) {
        LearnerRecordQuery query = LearnerRecordQuery.builder()
                .notEventTypes(List.of(COMPLETE_COURSE.getName()))
                .learnerRecordTypes(List.of(LearningResourceType.COURSE.name()))
                .learnerIds(Set.of(userId)).build();
        return learnerRecordService.getLearnerRecords(query);
    }

    public Map<String, LocalDateTime> getCompletionDatesForCourses(String userId, List<String> courseIds) {
        Map<String, LocalDateTime> completionDates = new HashMap<>();
        LearnerRecordEventQuery query = LearnerRecordEventQuery.builder()
                .eventTypes(List.of(COMPLETE_COURSE.getName()))
                .resourceIds(courseIds)
                .userId(userId).build();
        getLearnerRecordEventsNormalisedMostRecent(query)
                .forEach((key, value) -> completionDates.put(key, value.getEventTimestamp()));
        return completionDates;
    }

    public Map<String, LearnerRecordEvent> getLearnerRecordEventsNormalisedMostRecent(LearnerRecordEventQuery learnerRecordEventQuery) {
        Map<String, LearnerRecordEvent> eventMap = new HashMap<>();
        learnerRecordService.getLearnerRecordEvents(learnerRecordEventQuery)
                .forEach(e -> {
                    LearnerRecordEvent event = eventMap.get(e.getResourceId());
                    if (event == null) {
                        eventMap.put(e.getResourceId(), e);
                    } else {
                        if (e.getEventTimestamp().isAfter(event.getEventTimestamp())) {
                            eventMap.put(e.getResourceId(), e);
                        }
                    }
                });
        return eventMap;
    }

    public Map<String, List<ModuleRecord>> getModuleRecordsForCourses(List<String> courseIds, List<ModuleRecordResourceId> moduleRecordIds) {
        Map<String, List<ModuleRecord>> map = courseIds.stream().collect(Collectors.toMap(s -> s, s -> new ArrayList<>()));
        learnerRecordService.getModuleRecords(moduleRecordIds)
                .forEach(mr -> {
                    List<ModuleRecord> mrs = map.get(mr.getCourseId());
                    mrs.add(mr);
                    map.put(mr.getCourseId(), mrs);
                });
        return map;
    }

}
