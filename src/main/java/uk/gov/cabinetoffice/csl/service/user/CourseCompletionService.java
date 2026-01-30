package uk.gov.cabinetoffice.csl.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.ActionResult;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.ActionResultService;
import uk.gov.cabinetoffice.csl.service.CourseActionService;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseCompletionService {
    private final CourseActionService courseActionService;
    private final ActionResultService actionResultService;

    public void completeCourse(Course course, User user, LocalDateTime completionDate) {
        String courseId = course.getId();
        log.debug("Preparing the actionResult to mark the course (courseId: {}) as " +
            "COMPLETED for the user (userId: {}, emailId: {}).",
            courseId, user.getId(), user.getEmail());
        ActionResult actionResult = courseActionService.completeCourse(course, user, completionDate);
        log.debug("actionResult is about to be process to mark the course (courseId: {}) as " +
            "COMPLETED for the user (userId: {}, emailId: {}). actionResult: {}",
            courseId, user.getId(), user.getEmail(), actionResult);
        actionResultService.processResults(actionResult);
        log.debug("actionResult is processed to mark the course (courseId: {}) as " +
            "COMPLETED for the user (userId: {}, emailId: {}). actionResult: {}",
            courseId, user.getId(), user.getEmail(), actionResult);
    }
}
