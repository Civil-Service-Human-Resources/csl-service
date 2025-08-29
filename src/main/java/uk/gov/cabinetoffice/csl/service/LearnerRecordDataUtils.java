package uk.gov.cabinetoffice.csl.service;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.LearningResourceType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEvent;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEventQuery;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordQuery;
import uk.gov.cabinetoffice.csl.service.learning.ModuleRecordCollection;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    public Map<String, ModuleRecordCollection> getModuleRecordsForCourses(List<String> courseIds, List<ModuleRecordResourceId> moduleRecordIds) {
        Map<String, ModuleRecordCollection> map = courseIds.stream().collect(Collectors.toMap(s -> s, s -> new ModuleRecordCollection()));
        learnerRecordService.getModuleRecords(moduleRecordIds)
                .forEach(mr -> {
                    ModuleRecordCollection mrs = map.get(mr.getCourseId());
                    mrs.add(mr);
                    if (mr.getUpdatedAt() != null && mr.getUpdatedAt().isAfter(mrs.getLatestUpdatedDate())) {
                        mrs.setLatestUpdatedDate(mr.getUpdatedAt());
                    }
                    if (mr.getCompletionDate() != null && mr.getCompletionDate().isAfter(mrs.getLatestCompletionDate())) {
                        mrs.setLatestCompletionDate(mr.getCompletionDate());
                    }
                    if (mr.getState().equals(State.COMPLETED)) {
                        mrs.getCompletedModules().add(mr.getModuleId());
                    } else {
                        mrs.getIncompleteModules().add(mr.getModuleId());
                    }
                    if (mr.isEventModule() && mrs.getBookedEventModule().isEmpty()) {
                        mrs.setBookedEventModule(mr);
                    }
                    map.put(mr.getCourseId(), mrs);
                });
        return map;
    }

}
