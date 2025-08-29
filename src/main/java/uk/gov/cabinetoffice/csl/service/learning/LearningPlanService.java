package uk.gov.cabinetoffice.csl.service.learning;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEvent;
import uk.gov.cabinetoffice.csl.domain.learning.learningPlan.BookedLearningPlanCourse;
import uk.gov.cabinetoffice.csl.domain.learning.learningPlan.LearningPlan;
import uk.gov.cabinetoffice.csl.domain.learning.learningPlan.LearningPlanCourse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.LearnerRecordDataUtils;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction.REMOVE_FROM_LEARNING_PLAN;

@Service
public class LearningPlanService {

    private final LearnerRecordDataUtils learnerRecordDataUtils;
    private final LearningCatalogueService learningCatalogueService;
    private final UserDetailsService userDetailsService;
    private final LearningPlanFactory learningPlanFactory;

    public LearningPlanService(LearnerRecordDataUtils learnerRecordDataUtils, LearningCatalogueService learningCatalogueService, UserDetailsService userDetailsService, LearningPlanFactory learningPlanFactory) {
        this.learnerRecordDataUtils = learnerRecordDataUtils;
        this.learningCatalogueService = learningCatalogueService;
        this.userDetailsService = userDetailsService;
        this.learningPlanFactory = learningPlanFactory;
    }

    public LearningPlan getLearningPlan(String uid) {
        User user = userDetailsService.getUserWithUid(uid);
        List<String> requiredLearning = learningCatalogueService.getRequiredLearningIdsForDepartments(user.getDepartmentCodes());
        Map<String, LearnerRecordEvent> latestEventForCourseMap = new HashMap<>();
        List<String> courseIds = learnerRecordDataUtils.getNonCompleteCourseRecords(user.getId())
                .stream().filter(lr -> !requiredLearning.contains(lr.getResourceId()))
                .peek(lr -> latestEventForCourseMap.put(lr.getResourceId(), lr.getLatestEvent()))
                .map(LearnerRecord::getResourceId)
                .toList();
        Map<String, Course> courses = new HashMap<>();
        List<ModuleRecordResourceId> moduleRecordIds = new ArrayList<>();
        learningCatalogueService.getCourses(courseIds)
                .forEach(course -> {
                    if (course.shouldBeDisplayed()) {
                        courses.put(course.getId(), course);
                        List<ModuleRecordResourceId> courseModuleRecordIds = course.getRequiredModuleIdsForCompletion()
                                .stream().map(mId -> new ModuleRecordResourceId(uid, mId)).toList();
                        moduleRecordIds.addAll(courseModuleRecordIds);
                    }
                });
        List<LearningPlanCourse> learningPlanCourses = new ArrayList<>();
        List<BookedLearningPlanCourse> bookedLearningPlanCourses = new ArrayList<>();
        learnerRecordDataUtils.getModuleRecordsForCourses(courseIds, moduleRecordIds)
                .forEach((courseId, requiredModuleRecords) -> {
                    Course course = courses.get(courseId);
                    if (course != null) {
                        LearnerRecordEvent latestEvent = latestEventForCourseMap.get(courseId);
                        if (!(latestEvent != null && latestEvent.getActionType().equals(REMOVE_FROM_LEARNING_PLAN)
                                && latestEvent.getEventTimestamp().isAfter(requiredModuleRecords.getLatestUpdatedDate()))) {
                            requiredModuleRecords.getBookedEventModule()
                                    .ifPresentOrElse(moduleRecord -> {
                                        if (!moduleRecord.equalsStates(State.SKIPPED, State.COMPLETED)) {
                                            learningPlanFactory.getBookedLearningPlanCourse(course, moduleRecord, requiredModuleRecords.isLastModuleToComplete(moduleRecord.getModuleId()))
                                                    .ifPresent(bookedLearningPlanCourses::add);
                                        }
                                    }, () -> {
                                        State state = requiredModuleRecords.isEmpty() ? State.NULL : State.IN_PROGRESS;
                                        learningPlanCourses.add(learningPlanFactory.getLearningPlanCourse(course, state));
                                    });
                        }
                    }
                });
        LearningPlan learningPlan = new LearningPlan(uid, bookedLearningPlanCourses, learningPlanCourses);
        learningPlan.sortCourses();
        return learningPlan;
    }
}
