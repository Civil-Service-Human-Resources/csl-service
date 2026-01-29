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
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.ActionResult;
import uk.gov.cabinetoffice.csl.domain.learning.Learning;
import uk.gov.cabinetoffice.csl.domain.learning.requiredLearning.RequiredLearning;
import uk.gov.cabinetoffice.csl.domain.learning.requiredLearning.RequiredLearningCourse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.ActionResultService;
import uk.gov.cabinetoffice.csl.service.CourseActionService;
import uk.gov.cabinetoffice.csl.service.LearnerRecordDataUtils;
import uk.gov.cabinetoffice.csl.service.csrs.OrganisationalUnitService;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.learningResources.course.CourseRecordService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequiredLearningService {

    private final CourseRecordService courseRecordService;
    private final LearnerRecordDataUtils learnerRecordDataUtils;
    private final LearningCatalogueService learningCatalogueService;
    private final OrganisationalUnitService organisationalUnitService;
    private final UserDetailsService userDetailsService;
    private final LearningFactory<RequiredLearningDisplayCourseFactory> requiredLearningFactory;
    private final CourseActionService courseActionService;
    private final ActionResultService actionResultService;

    public Learning getDetailedRequiredLearning(String userId) {
        User user = userDetailsService.getUserWithUid(userId);
        List<Course> requiredLearning = learningCatalogueService.getRequiredLearningForDepartments(user.getDepartmentCodes());
        List<String> courseIds = requiredLearning.stream().map(Course::getCacheableId).toList();
        Map<String, CourseRecord> courseRecords = courseRecordService.getCourseRecords(userId, courseIds)
                .stream().collect(Collectors.toMap(CourseRecord::getCourseId, c -> c));
        return requiredLearningFactory.buildDetailedLearning(requiredLearning, courseRecords, user);
    }

    public RequiredLearning getRequiredLearning(String uid, Boolean homepageCompleteRequiredCourses) {
        log.warn("homepageCompleteRequiredCourses: {}", homepageCompleteRequiredCourses);

        // 1. Get user details from CSRS
        User user = userDetailsService.getUserWithUid(uid);
        log.warn("user.getName(): {}", user.getName());

        // 2. Get required learnings from elasticsearch for user's department
        List<Course> requiredLearning = learningCatalogueService.getRequiredLearningForDepartments(user.getDepartmentCodes())
                .stream().filter(Course::shouldBeDisplayed).toList();

        // 3. Get most recent completion event date from the user's learner record event
        Map<String, LocalDateTime> completionDates = learnerRecordDataUtils
                .getCompletionDatesForCourses(uid, requiredLearning.stream().map(Course::getId).toList());

        Map<String, RequiredLearningCourse> requiredLearningCourses = new HashMap<>();
        List<ModuleRecordResourceId> moduleRecordIdsToFetch = new ArrayList<>();
        Map<String, List<String>> requiredModuleIdsForCompletion = new HashMap<>();

        // 4. Iterate over the required learning and if the learning period is present
        // then use it to find the start and end date of the learning period
        requiredLearning.forEach(course -> course.getLearningPeriodForUser(user)
                .ifPresent(lp -> {
                    LocalDateTime completionDate = completionDates.get(course.getId());

                    if (completionDate == null || lp.getStartDateAsDateTime().isAfter(completionDate)) {
                        // 5. If the completion event date for the course is not present
                        // or the learning period start date after the completion date
                        // then set the course status as NULL
                        // and add it to the requiredLearningCourses list to display on homepage
                        // also add all required moduleIds which need to be completed into moduleRecordIdsToFetch
                        RequiredLearningCourse requiredLearningCourse = new RequiredLearningCourse(
                                course.getId(), course.getTitle(), course.getShortDescription(),
                                course.getCourseType(), course.getDurationInSeconds(), course.getModules().size(),
                                course.getCost(), State.NULL, lp);
                        requiredLearningCourses.put(requiredLearningCourse.getId(), requiredLearningCourse);
                        moduleRecordIdsToFetch.addAll(course.getRequiredModuleIdsForCompletion().stream()
                                .map(id -> new ModuleRecordResourceId(uid, id)).toList());
                        requiredModuleIdsForCompletion.put(course.getId(), course.getRequiredModuleIdsForCompletion());
                    }
                }));
        log.warn("requiredLearningCourses-1: {}", requiredLearningCourses);

        if (!moduleRecordIdsToFetch.isEmpty()) {
            // 6. If required moduleIds exist then get the moduleRecords from the user's learner record
            Map<String, ModuleRecordCollection> moduleRecords = learnerRecordDataUtils.getModuleRecordsForCourses(
                    new ArrayList<>(requiredLearningCourses.keySet()), moduleRecordIdsToFetch);

            // 7. If the latest update date of the module for the user is after the start date of the learning period
            // then set the course status as IN_PROGRESS
            Iterator<Map.Entry<String, RequiredLearningCourse>> requiredLearningCourseEntryIterator = requiredLearningCourses.entrySet().iterator();
            while (requiredLearningCourseEntryIterator.hasNext()) {
                Map.Entry<String, RequiredLearningCourse> requiredLearningCourseEntry = requiredLearningCourseEntryIterator.next();
                String requiredLearningCourseId = requiredLearningCourseEntry.getKey();
                RequiredLearningCourse requiredLearningCourse = requiredLearningCourseEntry.getValue();
                ModuleRecordCollection moduleRecordsCollection = moduleRecords.get(requiredLearningCourseId);

                if (moduleRecordsCollection.getLatestUpdatedDate()
                        .isAfter(requiredLearningCourse.getLearningPeriod().getStartDateAsDateTime())) {
                    requiredLearningCourse.setStatus(State.IN_PROGRESS);
                }

                List<String> requiredModuleIdsForCompletionForTheCourse = requiredModuleIdsForCompletion.get(requiredLearningCourseId);
                List<String> requiredModuleIdsLeftForCompletionForTheCourse = moduleRecordsCollection.getRequiredIdsLeftForCompletion(requiredModuleIdsForCompletionForTheCourse);

                log.warn("requiredLearningCourseId: {}, " +
                        "requiredLearningCourseStatus: {}, " +
                        "Total required modules: {}, moduleIds: {}, " +
                        "Required modules left for completion: {}, moduleIds: {}, " +
                        "Required modules completed: {}",
                        requiredLearningCourseId,
                        requiredLearningCourse.getStatus(),
                        requiredModuleIdsForCompletionForTheCourse.size(),
                        requiredModuleIdsForCompletionForTheCourse,
                        requiredModuleIdsLeftForCompletionForTheCourse.size(),
                        requiredModuleIdsLeftForCompletionForTheCourse,
                        requiredModuleIdsForCompletionForTheCourse.size() - requiredModuleIdsLeftForCompletionForTheCourse.size());

                // 8. Check if homepageCompleteRequiredCourses is true
                // and completion event is missing
                // and all the required modules are completed
                // then write the log entry for the course completion
                // and remove it from the requiredLearningCourses list
                // then set the course status to COMPLETED
                // and create the completion event using the latest module completion date in the learner DB
                // and create the entry in the completion reporting DB
                LocalDateTime completionDate = completionDates.get(requiredLearningCourseId);
                if (homepageCompleteRequiredCourses
                    && completionDate == null
                    && requiredModuleIdsLeftForCompletionForTheCourse.isEmpty()) {
                    log.warn("requiredLearningCourseId: {}, requiredLearningCourse: {}, moduleRecordsCollection: {}. " +
                            "This course status should be marked as COMPLETED.",
                            requiredLearningCourseId, requiredLearningCourse, moduleRecordsCollection);
                    requiredLearningCourseEntryIterator.remove(); // safe removal from Map
                    requiredLearningCourse.setStatus(State.COMPLETED);
                    log.warn("requiredLearningCourseId: {}, requiredLearningCourse: {}. " +
                            "This course status is updated to COMPLETED and it is removed from the homepage course list.",
                            requiredLearningCourseId, requiredLearningCourse);
                    Course course = requiredLearning.stream()
                            .filter(c -> requiredLearningCourseId.equals(c.getId()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Course not found for id: " + requiredLearningCourseId));
                    LocalDateTime latestModuleCompletionDate = moduleRecordsCollection.getLatestCompletionDate();
                    log.warn("requiredLearningCourseId: {}, latestModuleCompletionDate: {} course: {}. " +
                                    "latestModuleCompletionDate and course for the course to be marked as COMPLETED.",
                            requiredLearningCourseId, latestModuleCompletionDate, course);
                    // 9. Create the completion event and the completion report entry
                    log.warn("requiredLearningCourseId: {}. Preparing the actionResult to mark the course as COMPLETED.",
                            requiredLearningCourseId);
                    // TODO: Uncomment the following lines after updating the Integration test:
                    // RequiredLearningTest.testGetRequiredLearningUpdateCompletedStatusForMissingCompletionEvent()
//                    ActionResult actionResult = courseActionService.completeCourse(course, user, latestModuleCompletionDate);
//                    log.warn("requiredLearningCourseId: {}, actionResult: {}. actionResult about to be processed for the course to mark it as COMPLETED.",
//                            requiredLearningCourseId, actionResult);
//                    actionResultService.processResults(actionResult);
//                    log.warn("requiredLearningCourseId: {}, actionResult: {}. actionResult is processed for the course to mark it as COMPLETED.",
//                            requiredLearningCourseId, actionResult);
                }
            }
        }
        log.warn("requiredLearningCourses-2: {}", requiredLearningCourses);

        // 10. requiredLearningCourses are sorted and returned by wrapping in a response object
        RequiredLearning requiredLearningResponse = new RequiredLearning(uid, new ArrayList<>(requiredLearningCourses.values()));
        requiredLearningResponse.sortCourses();
        log.warn("requiredLearningResponse: {}", requiredLearningResponse);
        return requiredLearningResponse;
    }

    public RequiredLearningMapResponse getRequiredLearningMapForOrganisations(GetRequiredLearningForDepartmentsParams params) {
        Map<Long, List<CourseWithTitle>> result = new HashMap<>();
        Map<Long, List<OrganisationalUnit>> orgHierarchies = organisationalUnitService.getHierarchies(params.getOrganisationIds());
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
