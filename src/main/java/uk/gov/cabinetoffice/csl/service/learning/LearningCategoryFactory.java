package uk.gov.cabinetoffice.csl.service.learning;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCategories;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCategory;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagSubCategories;
import uk.gov.cabinetoffice.csl.controller.learning.model.Link;
import uk.gov.cabinetoffice.csl.domain.learning.LearningTagTaxonomy;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTag;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
public class LearningCategoryFactory {

    public LearningTagCategories buildCategories(Collection<LearningTag> tierOneTags) {
        Collection<LearningTagCategory> categories = tierOneTags
                .stream().map(lt -> new LearningTagCategory(lt.getName(), lt.getDescription(), lt.getUrlSlug(), List.of()))
                .sorted(Comparator.comparing(LearningTagCategory::getTitle))
                .toList();
        return new LearningTagCategories(categories);
    }

    public LearningTagSubCategories buildSubCategories(LearningTagTaxonomy taxonomy) {
        Collection<Link> parentLinks = taxonomy.parents().stream()
                .map(lt -> new Link(lt.getUrlSlug(), lt.getName())).toList();
        Collection<LearningTagCategory> categories = taxonomy.children()
                .stream().map(lt -> new LearningTagCategory(lt.category().getName(), lt.category().getDescription(), lt.category().getUrlSlug(),
                        lt.children().stream().map(descLt -> new Link(descLt.category().getUrlSlug(), descLt.category().getName()))
                                .sorted(Comparator.comparing(Link::getText))
                                .toList()))
                .sorted(Comparator.comparing(LearningTagCategory::getTitle))
                .toList();
        return new LearningTagSubCategories(categories, taxonomy.category().getName(),
                taxonomy.category().getDescription(), parentLinks);
    }
}
