package uk.gov.cabinetoffice.csl.controller.learning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.domain.learning.Learning;
import uk.gov.cabinetoffice.csl.service.learning.RequiredLearningService;

@Slf4j
@RestController
@RequestMapping("learning/required")
public class RequiredLearningController {

    private final RequiredLearningService requiredLearningService;

    public RequiredLearningController(RequiredLearningService requiredLearningService) {
        this.requiredLearningService = requiredLearningService;
    }

    @GetMapping("/detailed/{userId}")
    @ResponseBody
    public Learning getDetailedRequiredLearningForUser(@PathVariable String userId) {
        return requiredLearningService.getDetailedRequiredLearning(userId);
    }

}
