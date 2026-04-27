package uk.gov.cabinetoffice.csl.controller.learning;

import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.learning.model.GetSuggestedLearningParams;
import uk.gov.cabinetoffice.csl.controller.learning.model.SuggestedLearning;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;
import uk.gov.cabinetoffice.csl.service.learning.SuggestedLearningService;

@RestController
@RequestMapping("learning/catalogue")
public class LearningCatalogueController {

    private final SuggestedLearningService suggestedLearningService;
    private final IUserAuthService userAuthService;

    public LearningCatalogueController(SuggestedLearningService suggestedLearningService, IUserAuthService userAuthService) {
        this.suggestedLearningService = suggestedLearningService;
        this.userAuthService = userAuthService;
    }

    @ResponseBody
    @GetMapping("suggestions")
    public SuggestedLearning getSuggestedLearning(GetSuggestedLearningParams params) {
        return getSuggestedLearning(userAuthService.getUsername(), params);
    }

    @ResponseBody
    @GetMapping("suggestions/{uid}")
    public SuggestedLearning getSuggestedLearning(@PathVariable String uid, GetSuggestedLearningParams params) {
        return suggestedLearningService.getSuggestedLearningForUser(uid, params.getSize());
    }

}
