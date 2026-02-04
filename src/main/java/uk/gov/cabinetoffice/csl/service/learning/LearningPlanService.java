package uk.gov.cabinetoffice.csl.service.learning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEvent;
import uk.gov.cabinetoffice.csl.domain.learning.learningPlan.BookedLearningPlanCourse;
import uk.gov.cabinetoffice.csl.domain.learning.learningPlan.LearningPlan;
import uk.gov.cabinetoffice.csl.domain.learning.learningPlan.LearningPlanCourse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.LearnerRecordDataUtils;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.user.CourseCompletionService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

import java.time.LocalDateTime;
import java.util.*;

import static uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction.REMOVE_FROM_LEARNING_PLAN;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearningPlanService {

    private final LearnerRecordDataUtils learnerRecordDataUtils;
    private final LearningCatalogueService learningCatalogueService;
    private final UserDetailsService userDetailsService;
    private final LearningPlanFactory learningPlanFactory;
    private final CourseCompletionService courseCompletionService;

    public LearningPlan getLearningPlan(String uid, Boolean homepageCompleteLearningPlanCourses) {
        User user = userDetailsService.getUserWithUid(uid);
        List<String> requiredLearning = learningCatalogueService.getRequiredLearningIdsForDepartments(user.getDepartmentCodes());
        Map<String, LearnerRecordEvent> latestEventForCourseMap = new HashMap<>();
        List<String> nonCompleteCourseRecordIds = learnerRecordDataUtils.getNonCompleteCourseRecords(user.getId())
                .stream().filter(lr -> !requiredLearning.contains(lr.getResourceId()))
                .peek(lr -> latestEventForCourseMap.put(lr.getResourceId(), lr.getLatestEvent()))
                .map(LearnerRecord::getResourceId)
                .toList();
        Map<String, Course> coursesToBeDisplayed = new HashMap<>();
        List<ModuleRecordResourceId> moduleRecordIds = new ArrayList<>();
        learningCatalogueService.getCourses(nonCompleteCourseRecordIds)
                .forEach(nonCompleteCourse -> {
                    if (nonCompleteCourse.shouldBeDisplayed()) {
                        coursesToBeDisplayed.put(nonCompleteCourse.getId(), nonCompleteCourse);
                        List<ModuleRecordResourceId> requiredModuleIdsForCompletion = nonCompleteCourse.getRequiredModuleIdsForCompletion()
                                .stream().map(mId -> new ModuleRecordResourceId(uid, mId)).toList();
                        moduleRecordIds.addAll(requiredModuleIdsForCompletion);
                    }
                });
        List<LearningPlanCourse> learningPlanCourses = new ArrayList<>();
        List<BookedLearningPlanCourse> bookedLearningPlanCourses = new ArrayList<>();
        learnerRecordDataUtils.getModuleRecordsForCourses(nonCompleteCourseRecordIds, moduleRecordIds)
                .forEach((courseId, requiredModuleRecords) -> {
                    Course courseToBeDisplayed = coursesToBeDisplayed.get(courseId);
                    if (courseToBeDisplayed != null) {
                        LearnerRecordEvent latestEvent = latestEventForCourseMap.get(courseId);
                        if (!(latestEvent != null && latestEvent.getActionType().equals(REMOVE_FROM_LEARNING_PLAN)
                                && latestEvent.getEventTimestamp().isAfter(requiredModuleRecords.getLatestUpdatedDate()))) {
                            coursesToBeDisplayed(homepageCompleteLearningPlanCourses, requiredModuleRecords, courseToBeDisplayed,
                                    bookedLearningPlanCourses, user, learningPlanCourses);
                        }
                    }
                });
        LearningPlan learningPlan = new LearningPlan(uid, bookedLearningPlanCourses, learningPlanCourses);
        learningPlan.sortCourses();
        return learningPlan;
    }

    private void coursesToBeDisplayed(Boolean homepageCompleteLearningPlanCourses,
                                      ModuleRecordCollection requiredModuleRecords,
                                      Course courseToBeDisplayed,
                                      List<BookedLearningPlanCourse> bookedLearningPlanCourses,
                                      User user, List<LearningPlanCourse> learningPlanCourses) {
        Optional<ModuleRecord> moduleRecord = requiredModuleRecords.getModuleRecord();
        moduleRecord
            .ifPresentOrElse(requiredModuleRecord -> {
                if (!requiredModuleRecord.equalsStates(State.SKIPPED, State.COMPLETED)) {
                    learningPlanFactory.getBookedLearningPlanCourse(courseToBeDisplayed, requiredModuleRecords)
                            .ifPresent(bookedLearningPlanCourses::add);
                }
            }, () -> {
                State state = requiredModuleRecords.isEmpty() ? State.NULL : State.IN_PROGRESS;
                LocalDateTime latestModuleCompletionDate = requiredModuleRecords.getLatestCompletionDate();
                List<String> requiredModuleIdsForCompletion = courseToBeDisplayed.getRequiredModuleIdsForCompletion();
                List<String> requiredModuleIdsLeftForCompletion = requiredModuleRecords.getRequiredIdsLeftForCompletion(requiredModuleIdsForCompletion);
                if (requiredModuleIdsLeftForCompletion.isEmpty()) {
                    log.info("homepageCompleteLearningPlanCourses: {}", homepageCompleteLearningPlanCourses);
                    if (homepageCompleteLearningPlanCourses) {
                        log.info("All the required modules were completed for " +
                                "the course (courseId: {}) in the learning plan for " +
                                "the user (userid: {}, email: {}), but " +
                                "the completion event is missing therefore the status of this course is being " +
                                "auto-marked as COMPLETED with the completion date same as the latestModuleCompletionDate: {}. " +
                                "Following are the course and moduleRecordsCollection details. " +
                                "courseToBeDisplayed: {}, moduleRecordsCollection: {}",
                                courseToBeDisplayed.getId(), user.getId(), user.getEmail(), latestModuleCompletionDate,
                                courseToBeDisplayed, requiredModuleRecords);
                                courseCompletionService.completeCourse(courseToBeDisplayed, user, latestModuleCompletionDate);
                        state = State.COMPLETED;
                        log.info("Now the course (courseId: {}) is auto-marked as COMPLETED for " +
                                "the user (userid: {}, email: {}), and " +
                                "the completion event is created with the completion date same as the latestModuleCompletionDate: {} " +
                                "because all the required modules were completed. " +
                                "Now the course is moved from the learning plan on homepage to the completed learner record. " +
                                "Following are the course and moduleRecordsCollection details. " +
                                "courseToBeDisplayed: {}, moduleRecordsCollection: {}",
                                courseToBeDisplayed.getId(), user.getId(), user.getEmail(), latestModuleCompletionDate,
                                courseToBeDisplayed, requiredModuleRecords);
                    } else {
                        log.info("All the required modules were completed for " +
                                "the course (courseId: {}) in the learning plan for " +
                                "the user (userid: {}, email: {}), but " +
                                "the completion event is missing therefore the status of this course should be " +
                                "auto-marked as COMPLETED with the completion date same as the latestModuleCompletionDate: {}, but " +
                                "the flag 'homepageCompleteLearningPlanCourses' is false therefore the course is not auto-marked as COMPLETED. " +
                                "Following are the course and moduleRecordsCollection details. " +
                                "courseToBeDisplayed: {}, moduleRecordsCollection: {}",
                                courseToBeDisplayed.getId(), user.getId(), user.getEmail(), latestModuleCompletionDate,
                                courseToBeDisplayed, requiredModuleRecords);
                    }
                }
                if (!state.equals(State.COMPLETED)) {
                    learningPlanCourses.add(learningPlanFactory.getLearningPlanCourse(courseToBeDisplayed, state));
                }
            });
    }
}
