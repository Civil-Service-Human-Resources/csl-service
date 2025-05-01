package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.model.CourseResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.ActionWithId;
import uk.gov.cabinetoffice.csl.service.CourseActionService;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;

@Slf4j
@RestController
@RequestMapping("courses")
public class CourseController {

    private final CourseActionService courseActionService;
    private final ActionWithIdFactory actionWithIdFactory;
    private final IUserAuthService userAuthService;

    public CourseController(CourseActionService courseActionService, ActionWithIdFactory actionWithIdFactory, IUserAuthService userAuthService) {
        this.courseActionService = courseActionService;
        this.actionWithIdFactory = actionWithIdFactory;
        this.userAuthService = userAuthService;
    }

    @PostMapping("/{courseId}/remove_from_learning_plan")
    @ResponseBody
    public CourseResponse removeCourseFromLearningPlan(@PathVariable("courseId") String courseId) {
        ActionWithId action = actionWithIdFactory.create(courseId, userAuthService.getUsername(), CourseRecordAction.REMOVE_FROM_LEARNING_PLAN);
        return courseActionService.performCourseAction(action);
    }

    @PostMapping("/{courseId}/add_to_learning_plan")
    @ResponseBody
    public CourseResponse addCourseToLearningPlan(@PathVariable("courseId") String courseId) {
        ActionWithId action = actionWithIdFactory.create(courseId, userAuthService.getUsername(), CourseRecordAction.ADD_TO_LEARNING_PLAN);
        return courseActionService.performCourseAction(action);
    }

    @PostMapping("/{courseId}/remove_from_suggestions")
    @ResponseBody
    public CourseResponse removeCourseSuggestions(@PathVariable("courseId") String courseId) {
        ActionWithId action = actionWithIdFactory.create(courseId, userAuthService.getUsername(), CourseRecordAction.REMOVE_FROM_SUGGESTIONS);
        return courseActionService.performCourseAction(action);
    }
}
