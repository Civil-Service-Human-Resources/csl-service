package uk.gov.cabinetoffice.csl.service.learning;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.UserLearningCourse;
import uk.gov.cabinetoffice.csl.controller.model.UserLearningResponse;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEvent;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordPagedResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordQuery;
import uk.gov.cabinetoffice.csl.domain.learning.Learning;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.LearnerRecordDataUtils;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.learningResources.course.CourseRecordService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserLearningService {

    private final UserDetailsService userDetailsService;
    private final LearningCatalogueService learningCatalogueService;
    private final LearnerRecordService learnerRecordService;
    private final LearnerRecordDataUtils learnerRecordDataUtils;
    private final CourseRecordService courseRecordService;
    private final LearningFactory learningFactory;
    private final DisplayCourseFactory displayCourseFactory;

    public UserLearningResponse getOptionalLearningRecord(String uid, int page, int size) {
        User user = userDetailsService.getUserWithUid(uid);
        List<String> requiredLearningIds = learningCatalogueService.getRequiredLearningIdsForDepartments(user.getDepartmentCodes());

        LearnerRecordQuery query = LearnerRecordQuery.builder()
                .learnerIds(Set.of(uid))
                .notResourceIds(requiredLearningIds)
                .build();

        LearnerRecordPagedResponse response = learnerRecordService.getLearnerRecordPage(query, page, size);

        List<LearnerRecord> records = response.getContent() == null ? List.of() : response.getContent();
        List<String> courseIds = records.stream().map(LearnerRecord::getResourceId).toList();

        List<ModuleRecordResourceId> moduleRecordResourceIds = new ArrayList<>();
        learningCatalogueService.getCourses(courseIds).forEach(course -> course.getModules().forEach(
                module -> moduleRecordResourceIds.add(new ModuleRecordResourceId(uid, module.getId()))));
        Map<String, ModuleRecordCollection> moduleRecordsForCourses = learnerRecordDataUtils.getModuleRecordsForCourses(courseIds, moduleRecordResourceIds);

        Map<String, String> courseTitles = courseIds.isEmpty() ? Map.of() : learningCatalogueService.getCourseIdToTitleMap(courseIds);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM uuuu");

        List<UserLearningCourse> learningCourses = records.stream().map(record -> {
            UserLearningCourse c = new UserLearningCourse();
            c.setResourceId(record.getResourceId());
            c.setTitle(courseTitles.getOrDefault(record.getResourceId(), "Course title not found for resourceId: " + record.getResourceId()));

            LearnerRecordEvent latestEvent = record.getLatestEvent();
            if (latestEvent != null && CourseRecordAction.COMPLETE_COURSE.equals(latestEvent.getActionType())) {
                c.setStatus("Completed");
                c.setCompletionDate(latestEvent.getEventTimestamp().format(formatter));
            } else {
                c.setStatus("In progress");
                ModuleRecordCollection moduleRecords = moduleRecordsForCourses.get(c.getResourceId());
                if(moduleRecords.isEmpty()){
                    c.setStatus("");
                }
            }
            return c;
        }).sorted(Comparator.comparing(UserLearningCourse::getTitle, String.CASE_INSENSITIVE_ORDER)).toList();

        UserLearningResponse res = new UserLearningResponse();
        res.setLearning(learningCourses);
        res.setPage(response.getNumber() != null ? response.getNumber() : page);
        res.setSize(response.getSize() != null ? response.getSize() : size);
        res.setTotalResults(response.getTotalElements() != null ? response.getTotalElements() : 0);

        return res;
    }

    public Learning getDetailedLearning(String uid, List<String> courseIds) {
        User user = userDetailsService.getUserWithUid(uid);
        List<Course> courses = learningCatalogueService.getCourses(courseIds);
        Map<String, CourseRecord> courseRecords = courseRecordService.getCourseRecords(uid, courseIds)
                .stream().collect(Collectors.toMap(CourseRecord::getCourseId, c -> c));
        return learningFactory.buildDetailedLearning(displayCourseFactory, courses, courseRecords, user);
    }
}
