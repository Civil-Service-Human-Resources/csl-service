package uk.gov.cabinetoffice.csl.service.learning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.CourseWithTitle;
import uk.gov.cabinetoffice.csl.controller.model.GetRequiredLearningForDepartmentsParams;
import uk.gov.cabinetoffice.csl.controller.model.RequiredLearningMapResponse;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learning.Learning;
import uk.gov.cabinetoffice.csl.domain.learning.requiredLearning.RequiredLearning;
import uk.gov.cabinetoffice.csl.domain.learning.requiredLearning.RequiredLearningCourse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.LearnerRecordDataUtils;
import uk.gov.cabinetoffice.csl.service.csrs.OrganisationalUnitListService;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.learningResources.course.CourseRecordService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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
    private final OrganisationalUnitListService organisationalUnitListService;
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
        List<Course> requiredLearning = learningCatalogueService.getRequiredLearningForDepartments(user.getDepartmentCodes())
                .stream().filter(Course::shouldBeDisplayed).toList();
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
                                course.getCourseType(), course.getDurationInSeconds(), course.getModules().size(),
                                course.getCost(), State.NULL, lp);
                        requiredLearningCourses.add(requiredLearningCourse);
                        moduleRecordIdsToFetch.addAll(course.getRequiredModuleIdsForCompletion().stream()
                                .map(id -> new ModuleRecordResourceId(uid, id)).toList());
                    }
                }));
        if (!moduleRecordIdsToFetch.isEmpty()) {
            Map<String, ModuleRecordCollection> moduleRecords = learnerRecordDataUtils.getModuleRecordsForCourses(
                    requiredLearningCourses.stream().map(RequiredLearningCourse::getId).toList(), moduleRecordIdsToFetch);
            requiredLearningCourses
                    .forEach(course -> {
                        if (moduleRecords.get(course.getId()).getLatestUpdatedDate().isAfter(course.getLearningPeriod().getStartDateAsDateTime())) {
                            course.setStatus(State.IN_PROGRESS);
                        }
                    });
        }
        RequiredLearning requiredLearningResponse = new RequiredLearning(uid, requiredLearningCourses);
        requiredLearningResponse.sortCourses();
        return requiredLearningResponse;
    }

    public RequiredLearningMapResponse getRequiredLearningMapForOrganisations(GetRequiredLearningForDepartmentsParams params) {
        Map<Long, List<CourseWithTitle>> result = new HashMap<>();
        Map<Long, List<OrganisationalUnit>> orgHierarchies = organisationalUnitListService.getHierarchies(params.getOrganisationIds());
        Map<String, List<Course>> courseMap = learningCatalogueService.getRequiredLearningForDepartmentsMap(orgHierarchies.values().stream().flatMap(organisationalUnits -> organisationalUnits.stream().map(OrganisationalUnit::getCode)).collect(Collectors.toSet()));
        orgHierarchies.forEach((key, value) -> {
            List<CourseWithTitle> courses = new ArrayList<>();
            value.forEach(organisationalUnit -> {
                List<Course> coursesForOrgCode = courseMap.get(organisationalUnit.getCode());
                if (coursesForOrgCode != null) {
                    courses.addAll(coursesForOrgCode.stream().map(c -> new CourseWithTitle(c.getId(), c.getTitle())).toList());
                }
            });
            result.put(key, courses);
        });
        return new RequiredLearningMapResponse(result);
    }
}
