package uk.gov.cabinetoffice.csl.controller.learning;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.CourseLearningTagDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagDTO;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;

@RestController
@RequestMapping("/courses/{courseId}/learning-tags")
public class CourseLearningTagController {

    private final LearningCatalogueService learningCatalogueService;

    public CourseLearningTagController(LearningCatalogueService learningCatalogueService) {
        this.learningCatalogueService = learningCatalogueService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseLearningTagDto addLearningTagToCourse(@PathVariable("courseId") String courseId, @RequestBody LearningTagDTO learningTagDto) {
        return learningCatalogueService.addLearningTagToCourse(courseId, learningTagDto);
    }
}
