package uk.gov.cabinetoffice.csl.controller.learning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagOverview;
import uk.gov.cabinetoffice.csl.domain.BasicTaxonomyTree;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagDTO;
import uk.gov.cabinetoffice.csl.domain.taxonomy.FormattedTaxonomyItem;
import uk.gov.cabinetoffice.csl.domain.taxonomy.FormattedTaxonomyItems;
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
    public BasicTaxonomyTree getLearningTagOverviewTree() {
        log.info("Getting all organisational units as a tree");
        return learningCatalogueService.getLearningTagTree();

    }

    @GetMapping("/{learningTagId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LearningTagOverview getLearningTag(@PathVariable Long learningTagId) {
        return learningCatalogueService.getLearningTagOverview(learningTagId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LearningTagOverview createLearningTag(@RequestBody LearningTagDTO dto) {
        return learningCatalogueService.createLearningTag(dto);
    }

    @GetMapping(path = "/formatted_list", produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public FormattedTaxonomyItems<FormattedTaxonomyItem> getFormattedOrganisationalUnitNames() {
        return learningCatalogueService.getFormattedLearningTagNames();
    }
}
