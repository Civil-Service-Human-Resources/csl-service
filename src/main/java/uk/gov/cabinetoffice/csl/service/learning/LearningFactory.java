package uk.gov.cabinetoffice.csl.service.learning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.UserLearningCourse;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEvent;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayCourse;
import uk.gov.cabinetoffice.csl.domain.learning.Learning;
import uk.gov.cabinetoffice.csl.domain.learning.learningRecord.LearningRecord;
import uk.gov.cabinetoffice.csl.domain.learning.learningRecord.LearningRecordCourse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningFactory {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM uuuu");
    private final IDisplayCourseFactory displayCourseFactory;
    private final LearningRecordService learningRecordService;

    private UserLearningCourse buildCompletedUserLearningCourse(LearnerRecord record, String courseTitle,
                                                                LearnerRecordEvent completionEvent) {
        UserLearningCourse c = new UserLearningCourse();
        c.setResourceId(record.getResourceId());
        c.setTitle(courseTitle);
        c.setStatus("Completed");
        c.setCompletionDate(completionEvent.getEventTimestamp().format(formatter));
        return c;
    }

    private UserLearningCourse buildNonCompletedUserLearningCourse(LearnerRecord record, String courseTitle,
                                                                   ModuleRecordCollection moduleRecords) {
        UserLearningCourse c = new UserLearningCourse();
        c.setResourceId(record.getResourceId());
        c.setTitle(courseTitle);
        c.setStatus("");
        if (moduleRecords != null && moduleRecords.anyStateMatches(List.of(State.IN_PROGRESS, State.COMPLETED))) {
            c.setStatus("In progress");
        }
        return c;
    }

    public Collection<UserLearningCourse> buildUserLearning(Collection<LearnerRecord> records,
                                                            Map<String, ModuleRecordCollection> moduleRecordsForCourses,
                                                            Collection<Course> courses) {
        Map<String, String> courseTitles = courses.stream().collect(Collectors.toMap(Course::getCacheableId, Course::getTitle));
        return records.stream().map(record -> {
            String courseTitle = courseTitles.getOrDefault(record.getResourceId(), "Course details not found for resourceId: " + record.getResourceId());
            LearnerRecordEvent latestEvent = record.getLatestEvent();
            if (latestEvent != null && CourseRecordAction.COMPLETE_COURSE.equals(latestEvent.getActionType())) {
                return buildCompletedUserLearningCourse(record, courseTitle, latestEvent);
            } else {
                ModuleRecordCollection moduleRecords = moduleRecordsForCourses.get(record.getResourceId());
                return buildNonCompletedUserLearningCourse(record, courseTitle, moduleRecords);
            }
        }).toList();
    }

    public Learning buildDetailedLearning(List<Course> courses, Map<String, CourseRecord> courseRecords,
                                          User user) {

        LearningRecord learningRecord = learningRecordService.getLearningRecord(user.getId());
        List<LearningRecordCourse> userCompletedCourses = new ArrayList<>();
        if (learningRecord.getRequiredLearningRecord() != null && learningRecord.getRequiredLearningRecord().getCompletedCourses() != null) {
            userCompletedCourses.addAll(learningRecord.getRequiredLearningRecord().getCompletedCourses());
        }
        if (learningRecord.getOtherLearning() != null) {
            userCompletedCourses.addAll(learningRecord.getOtherLearning());
        }

        List<DisplayCourse> displayCourses = courses.stream().map(c -> {
            CourseRecord courseRecord = courseRecords.get(c.getCacheableId());

            LearningRecordCourse userLearningRecordCourse = null;
            Optional<LearningRecordCourse> completedCourse = userCompletedCourses
                    .stream()
                    .filter(course -> course.getId().equals(c.getId()))
                    .findFirst();

            if (completedCourse.isPresent()) {
                userLearningRecordCourse = completedCourse.get();
            }


            return displayCourseFactory.generateDetailedDisplayCourse(c, user, courseRecord, userLearningRecordCourse);
        }).toList();
        return new Learning(displayCourses);
    }

}
