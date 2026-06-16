package uk.gov.cabinetoffice.csl.controller.learning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagOverview;
import uk.gov.cabinetoffice.csl.domain.BasicTaxonomyTree;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;

@RestController
@RequestMapping("learning-tags")
@Slf4j
public class LearningTagController {


    private final LearningCatalogueService learningCatalogueService;

    public LearningTagController(LearningCatalogueService learningCatalogueService) {
        this.learningCatalogueService = learningCatalogueService;
    }

    @GetMapping(path = "/overview-tree", produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BasicTaxonomyTree getOrganisationalUnitOverviewTree() {
        log.info("Getting all organisational units as a tree");
        return learningCatalogueService.getLearningTagTree();

    }

    @GetMapping("/{learningTagId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LearningTagOverview getOrganisation(@PathVariable Long learningTagId) {
        return learningCatalogueService.getLearningTagOverview(learningTagId);
    }

}
