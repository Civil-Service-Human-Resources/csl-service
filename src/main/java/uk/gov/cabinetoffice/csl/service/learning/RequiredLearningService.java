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
import uk.gov.cabinetoffice.csl.service.csrs.OrganisationalUnitService;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.learningResources.course.CourseRecordService;
import uk.gov.cabinetoffice.csl.service.user.CourseCompletionService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequiredLearningService {

    private final CourseRecordService courseRecordService;
    private final LearnerRecordDataUtils learnerRecordDataUtils;
    private final LearningCatalogueService learningCatalogueService;
    private final OrganisationalUnitService organisationalUnitService;
    private final UserDetailsService userDetailsService;
    private final LearningFactory<RequiredLearningDisplayCourseFactory> requiredLearningFactory;
    private final CourseCompletionService courseCompletionService;

    public Learning getDetailedRequiredLearning(String userId) {
        User user = userDetailsService.getUserWithUid(userId);
        List<Course> requiredLearning = learningCatalogueService.getRequiredLearningForDepartments(user.getDepartmentCodes());
        List<String> courseIds = requiredLearning.stream().map(Course::getCacheableId).toList();
        Map<String, CourseRecord> courseRecords = courseRecordService.getCourseRecords(userId, courseIds)
                .stream().collect(Collectors.toMap(CourseRecord::getCourseId, c -> c));
        return requiredLearningFactory.buildDetailedLearning(requiredLearning, courseRecords, user);
    }

    public RequiredLearning getRequiredLearning(String uid, Boolean homepageCompleteRequiredCourses) {
        // 1. Get the user details
        User user = userDetailsService.getUserWithUid(uid);
        // 2. Get the required learnings from learning catalogue for user's department
        List<Course> requiredLearning = learningCatalogueService.getRequiredLearningForDepartments(user.getDepartmentCodes())
                .stream().filter(Course::shouldBeDisplayed).toList();
        // 3. Get the most recent completion event date from the user's learner record event
        Map<String, LocalDateTime> completionDates = learnerRecordDataUtils
                .getCompletionDatesForCourses(uid, requiredLearning.stream().map(Course::getId).toList());
        Map<String, RequiredLearningCourse> requiredLearningCourses = new HashMap<>();
        List<ModuleRecordResourceId> moduleRecordIdsToFetch = new ArrayList<>();
        Map<String, List<String>> requiredModuleIdsForCompletion = new HashMap<>();
        // 4. Iterate over the required learning
        // if the learning period is present
        // then use it to find the start and end date of the learning period
        requiredLearning.forEach(course -> course.getLearningPeriodForUser(user)
                .ifPresent(lp -> {
                    LocalDateTime completionDate = completionDates.get(course.getId());
                    // 5. If the completion event date for the course is not present
                    // or the learning period start date after the completion date
                    // then set the course status as NULL
                    // and add it to the requiredLearningCourses list to display on homepage
                    // and add all the required moduleIds into moduleRecordIdsToFetch which needed to be completed
                    if (completionDate == null || lp.getStartDateAsDateTime().isAfter(completionDate)) {
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
        // 6. If the required moduleIds exist
        // then get the moduleRecords from the user's learner record
        if (!moduleRecordIdsToFetch.isEmpty()) {
            Map<String, ModuleRecordCollection> moduleRecords = learnerRecordDataUtils.getModuleRecordsForCourses(
                    new ArrayList<>(requiredLearningCourses.keySet()), moduleRecordIdsToFetch);
            Iterator<Map.Entry<String, RequiredLearningCourse>> requiredLearningCourseEntryIterator = requiredLearningCourses.entrySet().iterator();
            while (requiredLearningCourseEntryIterator.hasNext()) {
                Map.Entry<String, RequiredLearningCourse> requiredLearningCourseEntry = requiredLearningCourseEntryIterator.next();
                String requiredLearningCourseId = requiredLearningCourseEntry.getKey();
                RequiredLearningCourse requiredLearningCourse = requiredLearningCourseEntry.getValue();
                ModuleRecordCollection requiredModuleRecords = moduleRecords.get(requiredLearningCourseId);
                // 7. If the latest update date of the module for the user is after the start date of the learning period
                // then set the course status as IN_PROGRESS
                if (requiredModuleRecords.getLatestUpdatedDate()
                        .isAfter(requiredLearningCourse.getLearningPeriod().getStartDateAsDateTime())) {
                    requiredLearningCourse.setStatus(State.IN_PROGRESS);
                }
                List<String> requiredModuleIdsForCompletionForTheCourse = requiredModuleIdsForCompletion.get(requiredLearningCourseId);
                List<String> requiredModuleIdsLeftForCompletionForTheCourse = requiredModuleRecords.getRequiredIdsLeftForCompletion(requiredModuleIdsForCompletionForTheCourse);
                LocalDateTime latestRequiredModuleCompletionDate = requiredModuleRecords.getLatestCompletionDate();
                LocalDateTime completionDate = completionDates.get(requiredLearningCourseId);
                // 8. If the completion event is missing
                // and all the required modules were completed
                // then write the log entry for the course completion status
                if (completionDate == null && requiredModuleIdsLeftForCompletionForTheCourse.isEmpty()) {
                    log.info("homepageCompleteRequiredCourses: {}", homepageCompleteRequiredCourses);
                    String requiredModuleRecordsDetails = "[" + requiredModuleRecords.stream()
                            .map(m ->
                                    "(ModuleId: " + m.getModuleId()
                                    + ", Uid: " + m.getUid()
                                    + ", CompletionDate: " + m.getCompletionDate()
                                    + ", UpdatedDate: " + m.getUpdatedAt()
                                    + ", Title: " + m.getModuleTitle()
                                    + ")"
                            ).collect(Collectors.joining(", ")) + "]";
                    // 9. If the homepageCompleteRequiredCourses is true
                    // then remove it from the requiredLearningCourses list
                    // and set the course status to COMPLETED
                    // and create the completion event using the latest module completion date
                    // and create the entry in the course completion report
                    // and write the detailed logs
                    if (homepageCompleteRequiredCourses) {
                        requiredLearningCourseEntryIterator.remove();
                        requiredLearningCourse.setStatus(State.COMPLETED);
                        Course course = requiredLearning.stream()
                                .filter(c -> requiredLearningCourseId.equals(c.getId()))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("Course not found for id: " + requiredLearningCourseId));
                        log.info("All the required modules were completed in the current learning period for " +
                                "the requiredLearningCourse (requiredLearningCourseId: {}) for " +
                                "the user (userid: {}, email: {}), but " +
                                "the completion event is missing therefore the status of this course is being " +
                                "auto-marked as COMPLETED with the completion date same as the latestRequiredModuleCompletionDate: {}. " +
                                "Following are the required learning course and the required modules records details. " +
                                "requiredLearningCourse: {}, requiredModuleRecordsDetails: {}",
                                requiredLearningCourseId, user.getId(), user.getEmail(), latestRequiredModuleCompletionDate,
                                requiredLearningCourse, requiredModuleRecordsDetails);
                        courseCompletionService.completeCourse(course, user, latestRequiredModuleCompletionDate);
                        log.info("Now the requiredLearningCourse (requiredLearningCourseId: {}) is auto-marked as COMPLETED for " +
                                "the user (userid: {}, email: {}), and " +
                                "the completion event is created with the completion date same as the latestRequiredModuleCompletionDate: {} " +
                                "because all the required modules were completed in the current learning period. " +
                                "Now the course is moved from the homepage mandatory courses list to the completed learner record. " +
                                "Following are the required learning course and the required modules records details. " +
                                "requiredLearningCourse: {}, requiredModuleRecordsDetails: {}",
                                requiredLearningCourseId, user.getId(), user.getEmail(), latestRequiredModuleCompletionDate,
                                requiredLearningCourse, requiredModuleRecordsDetails);
                    } else {
                        // 10. If the homepageCompleteRequiredCourses is false
                        // then write the detailed logs
                        // and don't take any action
                        log.info("All the required modules were completed in the current learning period for " +
                                "the course (requiredLearningCourseId: {}) for " +
                                "the user (userid: {}, email: {}), but " +
                                "the completion event is missing therefore the status of this course should be " +
                                "auto-marked as COMPLETED with the completion date same as the latestRequiredModuleCompletionDate: {}, but " +
                                "the flag 'homepageCompleteRequiredCourses' is false therefore the course is not auto-marked as COMPLETED. " +
                                "Following are the required learning course and the required modules records details. " +
                                "requiredLearningCourse: {}, requiredModuleRecordsDetails: {}",
                                requiredLearningCourseId, user.getId(), user.getEmail(), latestRequiredModuleCompletionDate,
                                requiredLearningCourse, requiredModuleRecordsDetails);
                    }
                }
            }
        }
        // 11. Wrap the requiredLearningCourses in requiredLearningResponse object
        RequiredLearning requiredLearningResponse = new RequiredLearning(uid, new ArrayList<>(requiredLearningCourses.values()));
        // 12. sort the requiredLearningCourses before returning
        requiredLearningResponse.sortCourses();
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
