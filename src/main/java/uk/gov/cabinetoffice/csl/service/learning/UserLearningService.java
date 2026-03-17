package uk.gov.cabinetoffice.csl.service.learning;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.UserLearningCourse;
import uk.gov.cabinetoffice.csl.controller.model.UserLearningResponse;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEvent;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordPagedResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordQuery;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserLearningService {

    private final UserDetailsService userDetailsService;
    private final LearningCatalogueService learningCatalogueService;
    private final LearnerRecordService learnerRecordService;

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
}
