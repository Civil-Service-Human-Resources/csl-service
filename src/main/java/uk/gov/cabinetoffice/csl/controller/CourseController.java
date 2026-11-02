package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.model.CourseResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.ActionWithId;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview.CourseOverview;
import uk.gov.cabinetoffice.csl.service.CourseActionService;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;
import uk.gov.cabinetoffice.csl.service.learning.LearningOverviewService;

@Slf4j
@RestController
@RequestMapping("courses")
public class CourseController {

    private final CourseActionService courseActionService;
    private final ActionWithIdFactory actionWithIdFactory;
    private final IUserAuthService userAuthService;
    private final LearningOverviewService learningOverviewService;

    public CourseController(CourseActionService courseActionService, ActionWithIdFactory actionWithIdFactory, IUserAuthService userAuthService, LearningOverviewService learningOverviewService) {
        this.courseActionService = courseActionService;
        this.actionWithIdFactory = actionWithIdFactory;
        this.userAuthService = userAuthService;
        this.learningOverviewService = learningOverviewService;
    }

    @GetMapping("{courseId}/overview")
    @ResponseBody
    public CourseOverview getCourseOverview(@PathVariable String courseId) {
        String uid = userAuthService.getUsername();
        return learningOverviewService.getCourseOverview(courseId, uid);
    }

    @PostMapping("/{courseId}/remove_from_learning_plan")
    @ResponseBody
    public CourseResponse removeCourseFromLearningPlan(@PathVariable String courseId) {
        ActionWithId action = actionWithIdFactory.create(courseId, userAuthService.getUsername(), CourseRecordAction.REMOVE_FROM_LEARNING_PLAN);
        return courseActionService.performCourseAction(action);
    }

    @PostMapping("/{courseId}/add_to_learning_plan")
    @ResponseBody
    public CourseResponse addCourseToLearningPlan(@PathVariable String courseId) {
        ActionWithId action = actionWithIdFactory.create(courseId, userAuthService.getUsername(), CourseRecordAction.MOVE_TO_LEARNING_PLAN);
        return courseActionService.performCourseAction(action);
    }

}
