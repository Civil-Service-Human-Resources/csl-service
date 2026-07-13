package uk.gov.cabinetoffice.csl.service.learningCatalogue;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagOverview;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTag;
import uk.gov.cabinetoffice.csl.service.ITaxonomyItemFactory;

@Service
public class LearningTagFactory implements ITaxonomyItemFactory<LearningTag, LearningTagOverview> {

    public LearningTagOverview createOverview(LearningTag learningTag) {
        return new LearningTagOverview(
                learningTag.getId(), learningTag.getName(), learningTag.getDescription(), learningTag.getCode(),
                learningTag.getUrlSlug(), learningTag.getFullUrl(), learningTag.getParentId(), learningTag.getParentName(), learningTag.isCategory(),
                learningTag.isArchived()
        );
    }

}
