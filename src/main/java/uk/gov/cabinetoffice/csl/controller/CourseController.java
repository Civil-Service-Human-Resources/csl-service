package uk.gov.cabinetoffice.csl.controller;

import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.model.CourseResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.ActionWithId;
import uk.gov.cabinetoffice.csl.service.CourseActionService;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;

@RestController
@RequestMapping("courses")
public class CourseController {

    private final CourseActionService courseActionService;
    private final ActionWithIdFactory actionWithIdFactory;
    private final IUserAuthService userAuthService;
    private final LearningCatalogueService learningCatalogueService;

    public CourseController(CourseActionService courseActionService, ActionWithIdFactory actionWithIdFactory, IUserAuthService userAuthService, LearningCatalogueService learningCatalogueService) {
        this.courseActionService = courseActionService;
        this.actionWithIdFactory = actionWithIdFactory;
        this.userAuthService = userAuthService;
        this.learningCatalogueService = learningCatalogueService;
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
        ActionWithId action = actionWithIdFactory.create(courseId, userAuthService.getUsername(), CourseRecordAction.MOVE_TO_LEARNING_PLAN);
        return courseActionService.performCourseAction(action);
    }

    @DeleteMapping("/{courseUid}/learning-tags/{learningTagCode}")
    @ResponseBody
    public void removeLearningTagFromCourse(@PathVariable("courseUid") String courseUid, @PathVariable("learningTagCode") String learningTagCode) {
        learningCatalogueService.removeLearningTagFromCourse(courseUid, learningTagCode);
    }
}
