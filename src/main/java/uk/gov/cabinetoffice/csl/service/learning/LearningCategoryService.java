package uk.gov.cabinetoffice.csl.service.learning;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCategories;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagSubCategories;
import uk.gov.cabinetoffice.csl.domain.learning.LearningTagTaxonomy;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTag;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningTagMapService;

import java.util.Collection;

@Service
public class LearningCategoryService {

    private final LearningTagMapService learningTagMapService;
    private final LearningCategoryFactory learningCategoryFactory;

    public LearningCategoryService(LearningTagMapService learningTagMapService, LearningCategoryFactory learningCategoryFactory) {
        this.learningTagMapService = learningTagMapService;
        this.learningCategoryFactory = learningCategoryFactory;
    }

    public LearningTagCategories getCategories() {
        Collection<LearningTag> tags = learningTagMapService.getTierOneUnarchivedHomepageTags();
        return learningCategoryFactory.buildCategories(tags);
    }

    public LearningTagSubCategories getCategories(String urlSlug) {
        LearningTagTaxonomy taxonomy = learningTagMapService.getUnarchivedHomepageTagsWithUrl(urlSlug);
        return learningCategoryFactory.buildSubCategories(taxonomy);
    }
}
