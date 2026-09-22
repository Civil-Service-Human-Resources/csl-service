package uk.gov.cabinetoffice.csl.controller.learning;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagUpdateRequest;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagUpdateResponse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.HyperlinkDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.HyperlinkSearchResults;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;

@RestController
@RequestMapping("learning-tags/{learningTagId}/hyperlinks")
@Slf4j
public class LearningTagHyperlinkController {

    private final LearningCatalogueService learningCatalogueService;

    public LearningTagHyperlinkController(LearningCatalogueService learningCatalogueService) {
        this.learningCatalogueService = learningCatalogueService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public HyperlinkSearchResults getHyperlinksForLearningTag(@PathVariable Long learningTagId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "20") int size) {
        return learningCatalogueService.getHyperlinksForLearningTag(learningTagId, page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public HyperlinkDto createHyperlink(@PathVariable Long learningTagId,
                                        @Valid @RequestBody HyperlinkDto request) {
        return learningCatalogueService.assignHyperlinkToLearningTag(learningTagId, request);
    }

    @GetMapping("/{hyperlinkId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public HyperlinkDto getHyperlink(@PathVariable Long learningTagId,
                                     @PathVariable Long hyperlinkId) {
        return learningCatalogueService.getHyperlink(learningTagId, hyperlinkId);
    }

    @PutMapping("/{hyperlinkId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public HyperlinkDto updateHyperlink(@PathVariable Long learningTagId,
                                        @PathVariable Long hyperlinkId,
                                        @Valid @RequestBody HyperlinkDto request) {
        return learningCatalogueService.updateHyperlink(learningTagId, hyperlinkId, request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LearningTagUpdateResponse deleteHyperlinksFromLearningTag(@PathVariable Long learningTagId,
                                                                     @RequestBody LearningTagUpdateRequest request) {
        return learningCatalogueService.deleteHyperlinksFromLearningTag(learningTagId, request);
    }

}
