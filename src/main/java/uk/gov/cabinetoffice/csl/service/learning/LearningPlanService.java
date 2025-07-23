package uk.gov.cabinetoffice.csl.service.learning;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
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

    private List<String> getCourseIdsForLearningPlan(User user) {
        List<String> requiredLearning = learningCatalogueService.getRequiredLearningIdsForDepartments(user.getDepartmentCodes());
        return learnerRecordDataUtils.getNonCompleteCourseRecords(user.getId())
                .stream().map(LearnerRecord::getResourceId)
                .filter(courseId -> !requiredLearning.contains(courseId))
                .toList();
    }

    public LearningPlan getLearningPlan(String uid) {
        User user = userDetailsService.getUserWithUid(uid);
        List<String> courseIds = getCourseIdsForLearningPlan(user);
        Map<String, Course> courses = new HashMap<>();
        List<ModuleRecordResourceId> moduleRecordIds = new ArrayList<>();
        learningCatalogueService.getCourses(courseIds)
                .forEach(course -> {
                    if (course.ShouldBeDisplayed()) {
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
                    learningPlanFactory.getBookedLearningPlanCourse(course, requiredModuleRecords)
                            .ifPresentOrElse(bookedLearningPlanCourses::add, () -> {
                                State state = requiredModuleRecords.isEmpty() ? State.NULL : State.IN_PROGRESS;
                                learningPlanCourses.add(learningPlanFactory.getLearningPlanCourse(course, state));
                            });
                });
        LearningPlan learningPlan = new LearningPlan(uid, bookedLearningPlanCourses, learningPlanCourses);
        learningPlan.sortCourses();
        return learningPlan;
    }
}
