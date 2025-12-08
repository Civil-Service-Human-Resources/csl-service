package uk.gov.cabinetoffice.csl.controller.learning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
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
    public LearningPlan getLearningPlanForUser() {
        return learningPlanService.getLearningPlan(userAuthService.getUsername());
    }

}

