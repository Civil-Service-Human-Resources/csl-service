package uk.gov.cabinetoffice.csl.controller.learning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCourseUpdateRequest;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCourseUpdateResponse;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagOverview;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagDTO;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagStateDTO;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseLearningTagSearchResults;
import uk.gov.cabinetoffice.csl.domain.taxonomy.BasicTaxonomyTree;
import uk.gov.cabinetoffice.csl.domain.taxonomy.FormattedTaxonomyItem;
import uk.gov.cabinetoffice.csl.domain.taxonomy.FormattedTaxonomyItems;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;

import java.util.List;

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

    @DeleteMapping("/{learningTagId}/courses")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LearningTagCourseUpdateResponse deleteCoursesFromLearningTag(@PathVariable Long learningTagId,
                                                                        @RequestBody LearningTagCourseUpdateRequest request) {
        return learningCatalogueService.deleteCoursesFromLearningTag(learningTagId, request);
    }

    @PostMapping("/{learningTagId}/courses")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public List<CourseDto> assignCoursesToLearningTag(@PathVariable Long learningTagId,
                                                      @RequestBody List<CourseDto> courses) {
        return learningCatalogueService.assignCoursesToLearningTag(learningTagId, courses);
    }
}
