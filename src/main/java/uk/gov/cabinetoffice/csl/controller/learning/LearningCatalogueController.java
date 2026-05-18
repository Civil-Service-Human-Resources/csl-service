package uk.gov.cabinetoffice.csl.controller.learning;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.learning.model.GetSuggestedLearningParams;
import uk.gov.cabinetoffice.csl.controller.learning.model.SuggestedLearning;
import uk.gov.cabinetoffice.csl.controller.model.PagedResults;
import uk.gov.cabinetoffice.csl.domain.learning.learningPlan.LearningPlanCourse;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;
import uk.gov.cabinetoffice.csl.service.learning.CSLCatalogueService;

@RestController
@RequestMapping("learning/catalogue")
public class LearningCatalogueController {

    private final CSLCatalogueService cslCatalogueService;
    private final IUserAuthService userAuthService;

    public LearningCatalogueController(CSLCatalogueService suggestedLearningService, IUserAuthService userAuthService) {
        this.cslCatalogueService = suggestedLearningService;
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
        return cslCatalogueService.getSuggestedLearningForUser(uid, params);
    }

    @ResponseBody
    @GetMapping("a-z/{letter:^[a-zA-Z]$}")
    public PagedResults<LearningPlanCourse> getLearningTitleStartsWith(@PathVariable String letter,
                                                                       @PageableDefault(size = 20, direction = Sort.Direction.ASC) Pageable pageableParams) {
        return cslCatalogueService.getCoursesForLetter(userAuthService.getUsername(), letter, pageableParams);
    }

}
