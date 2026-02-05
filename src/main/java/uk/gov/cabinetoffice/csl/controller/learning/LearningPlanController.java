package uk.gov.cabinetoffice.csl.controller.learning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.domain.learning.learningPlan.LearningPlan;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;
import uk.gov.cabinetoffice.csl.service.learning.LearningPlanService;

@Slf4j
@RestController
@RequestMapping("learning/plan")
public class LearningPlanController {

    private final LearningPlanService learningPlanService;
    private final IUserAuthService userAuthService;

    public LearningPlanController(LearningPlanService learningPlanService, IUserAuthService userAuthService) {
        this.learningPlanService = learningPlanService;
        this.userAuthService = userAuthService;
    }

    @GetMapping
    @ResponseBody
    public LearningPlan getLearningPlanForUser(
            @RequestParam(name = "HOMEPAGE_COMPLETE_LEARNING_PLAN_COURSES", defaultValue = "false")
            Boolean homepageCompleteLearningPlanCourses) {
        return learningPlanService.getLearningPlan(userAuthService.getUsername(), homepageCompleteLearningPlanCourses);
    }

    @GetMapping("/{uid}")
    public LearningPlan getLearningPlanByUid(
            @PathVariable("uid") String uid,
            @RequestParam(name = "HOMEPAGE_COMPLETE_LEARNING_PLAN_COURSES", defaultValue = "false")
            Boolean homepageCompleteLearningPlanCourses
    ) {
        return learningPlanService.getLearningPlan(uid, homepageCompleteLearningPlanCourses);
    }
}

