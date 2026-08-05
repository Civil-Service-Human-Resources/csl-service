package uk.gov.cabinetoffice.csl.service.learning;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCategories;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCategory;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTag;

import java.util.Collection;
import java.util.Comparator;

@Service
public class LearningCategoryFactory {

    public LearningTagCategories buildCategories(Collection<LearningTag> tierOneTags) {
        Collection<LearningTagCategory> categories = tierOneTags
                .stream().map(lt -> new LearningTagCategory(lt.getName(), lt.getDescription(), lt.getFullUrl()))
                .sorted(Comparator.comparing(LearningTagCategory::getTitle))
                .toList();
        return new LearningTagCategories(categories);
    }

}
