package uk.gov.cabinetoffice.csl.service.learningCatalogue;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagOverview;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTag;

@Service
public class LearningTagFactory {


    public LearningTagOverview createLearningTagOverview(LearningTag learningTag) {
        return new LearningTagOverview(
                learningTag.getId(), learningTag.getName(), learningTag.getDescription(), learningTag.getCode(),
                learningTag.getUrlSlug(), learningTag.getFullUrl(), learningTag.getParentId(), learningTag.getParentName(), learningTag.isCategoryTag(),
                learningTag.isArchived()
        );
    }
}
