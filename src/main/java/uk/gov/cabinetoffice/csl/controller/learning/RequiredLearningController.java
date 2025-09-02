package uk.gov.cabinetoffice.csl.controller.learning;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.model.GetRequiredLearningForDepartmentsParams;
import uk.gov.cabinetoffice.csl.controller.model.RequiredLearningMapResponse;
import uk.gov.cabinetoffice.csl.domain.learning.Learning;
import uk.gov.cabinetoffice.csl.domain.learning.requiredLearning.RequiredLearning;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;
import uk.gov.cabinetoffice.csl.service.learning.RequiredLearningService;

@Slf4j
@RestController
@RequestMapping("learning/required")
public class RequiredLearningController {

    private final RequiredLearningService requiredLearningService;
    private final IUserAuthService userAuthService;

    public RequiredLearningController(RequiredLearningService requiredLearningService, IUserAuthService userAuthService) {
        this.requiredLearningService = requiredLearningService;
        this.userAuthService = userAuthService;
    }

    @GetMapping("/for-departments")
    public RequiredLearningMapResponse getRequiredLearningForOrganisations(@Valid GetRequiredLearningForDepartmentsParams params) {
        return requiredLearningService.getRequiredLearningMapForOrganisations(params);
    }

    @GetMapping
    public RequiredLearning getRequiredLearning() {
        String uid = userAuthService.getUsername();
        return requiredLearningService.getRequiredLearning(uid);
    }

    @GetMapping("/detailed/{userId}")
    @ResponseBody
    public Learning getDetailedRequiredLearningForUser(@PathVariable String userId) {
        return requiredLearningService.getDetailedRequiredLearning(userId);
    }

}
