package uk.gov.cabinetoffice.csl.controller.learning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.learning.model.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseLearningTagSearchResults;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.HyperlinkSearchResults;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagDTO;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagStateDTO;
import uk.gov.cabinetoffice.csl.domain.taxonomy.BasicTaxonomyTree;
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

    @PutMapping("/{learningTagId}")
    @ResponseStatus(HttpStatus.OK)
    public LearningTagOverview updateLearningTag(@PathVariable Long learningTagId,
                                                 @RequestBody LearningTagDTO request
    ) {
        log.info("Update learning tag for id: {} and request: {}", learningTagId, request.toString());
        return learningCatalogueService.patchLearningTag(learningTagId, request);
    }

    @PutMapping("/{learningTagId}/state")
    @ResponseStatus(HttpStatus.OK)
    public LearningTagOverview updateState(@PathVariable Long learningTagId,
                                           @RequestBody LearningTagStateDTO request
    ) {
        log.info("Update learning tag for id: {} and request: {}", learningTagId, request.toString());
        return learningCatalogueService.updateState(learningTagId, request);
    }

    @GetMapping(path = "/formatted_list", produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public FormattedTaxonomyItems<FormattedTaxonomyItem> getFormattedOrganisationalUnitNames() {
        return learningCatalogueService.getFormattedLearningTagNames();
    }

    @GetMapping("/{learningTagId}/courses")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CourseLearningTagSearchResults getCoursesForLearningTag(@PathVariable Long learningTagId,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "20") int size) {
        return learningCatalogueService.getCoursesForLearningTag(learningTagId, page, size);
    }

    @GetMapping("/{learningTagId}/hyperlinks")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public HyperlinkSearchResults getHyperlinksForLearningTag(@PathVariable Long learningTagId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "20") int size) {
        return learningCatalogueService.getHyperlinksForLearningTag(learningTagId, page, size);
    }

    @DeleteMapping("/{learningTagId}/hyperlinks")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LearningTagUpdateResponse deleteHyperlinksFromLearningTag(@PathVariable Long learningTagId,
                                                                     @RequestBody LearningTagUpdateRequest request) {
        return learningCatalogueService.deleteHyperlinksFromLearningTag(learningTagId, request);
    }

    @DeleteMapping("/{learningTagId}/courses")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LearningTagUpdateResponse deleteCoursesFromLearningTag(@PathVariable Long learningTagId,
                                                                  @RequestBody LearningTagUpdateRequest request) {
        return learningCatalogueService.deleteCoursesFromLearningTag(learningTagId, request);
    }

    @PostMapping("/courses")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public BulkLearningTagUpdateResponse assignCoursesToLearningTags(@RequestBody LearningTagCourseAssignmentRequest request) {
        return learningCatalogueService.assignCoursesToLearningTags(request);
    }
}
