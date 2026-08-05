package uk.gov.cabinetoffice.csl.controller.learning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCategories;
import uk.gov.cabinetoffice.csl.service.learning.LearningCategoryService;

@RestController
@RequestMapping("learning/categories")
@Slf4j
public class LearningCategoriesController {

    private final LearningCategoryService learningCategoryService;

    public LearningCategoriesController(LearningCategoryService learningCategoryService) {
        this.learningCategoryService = learningCategoryService;
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public LearningTagCategories getCategories() {
        return learningCategoryService.getCategories();
    }

}
