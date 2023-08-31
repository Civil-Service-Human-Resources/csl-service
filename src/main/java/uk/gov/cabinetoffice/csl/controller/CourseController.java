package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cabinetoffice.csl.controller.model.CourseResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.CourseRecordAction;
import uk.gov.cabinetoffice.csl.service.CourseService;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;

@Slf4j
@RestController
@RequestMapping("courses")
public class CourseController {

    private final CourseService courseService;
    private final IUserAuthService userAuthService;

    public CourseController(CourseService courseService, IUserAuthService userAuthService) {
        this.courseService = courseService;
        this.userAuthService = userAuthService;
    }

    @PostMapping("/{courseId}/remove_from_learning_plan")
    public ResponseEntity<CourseResponse> removeCourseFromLearningPlan(@PathVariable("courseId") String courseId) {
        String learnerId = userAuthService.getUsername();
        CourseResponse response = courseService.processCourseRecordAction(learnerId, courseId, CourseRecordAction.REMOVE_FROM_LEARNING_PLAN);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{courseId}/add_to_learning_plan")
    public ResponseEntity<CourseResponse> addCourseToLearningPlan(@PathVariable("courseId") String courseId) {
        String learnerId = userAuthService.getUsername();
        CourseResponse response = courseService.processCourseRecordAction(learnerId, courseId, CourseRecordAction.ADD_TO_LEARNING_PLAN);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{courseId}/remove_from_suggestions")
    public ResponseEntity<CourseResponse> removeCourseSuggestions(@PathVariable("courseId") String courseId) {
        String learnerId = userAuthService.getUsername();
        CourseResponse response = courseService.processCourseRecordAction(learnerId, courseId, CourseRecordAction.REMOVE_FROM_SUGGESTIONS);
        return ResponseEntity.ok(response);
    }
}
