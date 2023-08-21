package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cabinetoffice.csl.controller.model.AddCourseToLearningPlanResponse;
import uk.gov.cabinetoffice.csl.controller.model.RemoveCourseFromLearningPlanResponse;
import uk.gov.cabinetoffice.csl.controller.model.RemoveCourseFromSuggestionsResponse;
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
    public ResponseEntity<RemoveCourseFromLearningPlanResponse> removeCourseFromLearningPlan(@PathVariable("courseId") String courseId) {
        String learnerId = userAuthService.getUsername();
        RemoveCourseFromLearningPlanResponse response = courseService.removeFromLearningPlan(learnerId, courseId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{courseId}/add_to_learning_plan")
    public ResponseEntity<AddCourseToLearningPlanResponse> addCourseToLearningPlan(@PathVariable("courseId") String courseId) {
        String learnerId = userAuthService.getUsername();
        AddCourseToLearningPlanResponse response = courseService.addCourseToLearningPlan(learnerId, courseId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{courseId}/remove_from_suggestions")
    public ResponseEntity<RemoveCourseFromSuggestionsResponse> removeCourseSuggestions(@PathVariable("courseId") String courseId) {
        String learnerId = userAuthService.getUsername();
        RemoveCourseFromSuggestionsResponse response = courseService.removeCourseFromSuggestions(learnerId, courseId);
        return ResponseEntity.ok(response);
    }
}
