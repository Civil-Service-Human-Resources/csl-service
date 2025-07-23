package uk.gov.cabinetoffice.csl.service.learning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learning.Learning;
import uk.gov.cabinetoffice.csl.domain.learning.requiredLearning.RequiredLearning;
import uk.gov.cabinetoffice.csl.domain.learning.requiredLearning.RequiredLearningCourse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.LearnerRecordDataUtils;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.learningResources.course.CourseRecordService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequiredLearningService {

    private final CourseRecordService courseRecordService;
    private final LearnerRecordDataUtils learnerRecordDataUtils;
    private final LearningCatalogueService learningCatalogueService;
    private final UserDetailsService userDetailsService;
    private final LearningFactory<RequiredLearningDisplayCourseFactory> requiredLearningFactory;

    public Learning getDetailedRequiredLearning(String userId) {
        User user = userDetailsService.getUserWithUid(userId);
        List<Course> requiredLearning = learningCatalogueService.getRequiredLearningForDepartments(user.getDepartmentCodes());
        List<String> courseIds = requiredLearning.stream().map(Course::getCacheableId).toList();
        Map<String, CourseRecord> courseRecords = courseRecordService.getCourseRecords(userId, courseIds)
                .stream().collect(Collectors.toMap(CourseRecord::getCourseId, c -> c));
        return requiredLearningFactory.buildDetailedLearning(requiredLearning, courseRecords, user);
    }

    public RequiredLearning getRequiredLearning(String uid) {
        User user = userDetailsService.getUserWithUid(uid);
        List<Course> requiredLearning = learningCatalogueService.getRequiredLearningForDepartments(user.getDepartmentCodes());
        Map<String, LocalDateTime> completionDates = learnerRecordDataUtils
                .getCompletionDatesForCourses(uid, requiredLearning.stream().map(Course::getId).toList());
        List<RequiredLearningCourse> requiredLearningCourses = new ArrayList<>();
        List<ModuleRecordResourceId> moduleRecordIdsToFetch = new ArrayList<>();
        requiredLearning.forEach(course -> course.getLearningPeriodForUser(user)
                .ifPresent(lp -> {
                    LocalDateTime completionDate = completionDates.get(course.getId());
                    if (completionDate == null || lp.getStartDateAsDateTime().isAfter(completionDate)) {
                        RequiredLearningCourse requiredLearningCourse = new RequiredLearningCourse(
                                course.getId(), course.getTitle(), course.getShortDescription(),
                                course.getCourseType(), course.getDurationInMinutes(), course.getModules().size(),
                                course.getCost(), State.NULL, lp);
                        requiredLearningCourses.add(requiredLearningCourse);
                        moduleRecordIdsToFetch.addAll(course.getRequiredModuleIdsForCompletion().stream()
                                .map(id -> new ModuleRecordResourceId(uid, id)).toList());
                    }
                }));
        if (!moduleRecordIdsToFetch.isEmpty()) {
            Map<String, List<ModuleRecord>> moduleRecords = learnerRecordDataUtils.getModuleRecordsForCourses(
                    requiredLearningCourses.stream().map(RequiredLearningCourse::getId).toList(), moduleRecordIdsToFetch);
            requiredLearningCourses
                    .forEach(course -> course.setStatusForModules(moduleRecords.get(course.getId())));
        }
        RequiredLearning requiredLearningResponse = new RequiredLearning(uid, requiredLearningCourses);
        requiredLearningResponse.sortCourses();
        return requiredLearningResponse;
    }
}
