package uk.gov.cabinetoffice.csl.service.learning;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCategories;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCategory;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagSubCategories;
import uk.gov.cabinetoffice.csl.controller.learning.model.Link;
import uk.gov.cabinetoffice.csl.controller.model.PagedResults;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learning.LearningTagTaxonomy;
import uk.gov.cabinetoffice.csl.domain.learning.learningPlan.BasicCourse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseLearningTagSearchResults;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.HyperlinkDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.HyperlinkSearchResults;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTag;

import java.util.*;

@Service
public class LearningCategoryFactory {

    public LearningTagCategories buildCategories(Collection<LearningTagTaxonomy> tierOneTaxonomies) {
        Collection<LearningTagCategory> categories = tierOneTaxonomies
                .stream().map(tax -> new LearningTagCategory(
                        tax.category().getName(),
                        tax.category().getDescription(),
                        tax.category().getUrlSlug(),
                        tax.children().stream()
                                .filter(childTax -> childTax.category().showOnHomepage())
                                .map(childTax -> new Link(childTax.category().getUrlSlug(), childTax.category().getName()))
                                .sorted(Comparator.comparing(Link::getText))
                                .toList(),
                        tax.category().getCourseCount(),
                        tax.category().getLinkCount()
                ))
                .sorted(Comparator.comparing(LearningTagCategory::getTitle))
                .toList();
        return new LearningTagCategories(categories);
    }

    public LearningTagSubCategories buildSubCategories(LearningTagTaxonomy taxonomy) {
        List<Link> parentLinks = new ArrayList<>(taxonomy.parents().stream()
                .map(lt -> new Link(lt.getUrlSlug(), lt.getName()))
                .toList());
        Collections.reverse(parentLinks);
        Collection<LearningTagCategory> categories = taxonomy.children()
                .stream().map(lt -> new LearningTagCategory(lt.category().getName(), lt.category().getDescription(), lt.category().getUrlSlug(),
                        lt.children().stream()
                                .filter(descLt -> descLt.category().showOnHomepage())
                                .map(descLt -> new Link(descLt.category().getUrlSlug(), descLt.category().getName()))
                                .sorted(Comparator.comparing(Link::getText))
                                .toList(), lt.category().getCourseCount(), lt.category().getLinkCount()))
                .sorted(Comparator.comparing(LearningTagCategory::getTitle))
                .toList();
        return new LearningTagSubCategories(categories, taxonomy.category().getName(),
                taxonomy.category().getDescription(), taxonomy.category().getUrlSlug(), parentLinks, taxonomy.category().getCourseCount(), PagedResults.emptyResults(),
                taxonomy.category().getLinkCount(), PagedResults.emptyResults());
    }

    public LearningTagSubCategories buildSubCategories(LearningTagTaxonomy taxonomy, CourseLearningTagSearchResults courses, Map<String, State> courseStates) {
        LearningTagSubCategories learningTagSubCategories = buildSubCategories(taxonomy);
        List<BasicCourse> formattedCourses = courses.getResults().stream().map(c -> new BasicCourse(c.getId(), c.getTitle(), c.getShortDescription(), courseStates.get(c.getId()))).toList();
        PagedResults<BasicCourse> results = new PagedResults<>(formattedCourses, courses.getPage(), courses.getSize(), courses.getTotalResults());
        learningTagSubCategories.setCourses(results);
        return learningTagSubCategories;
    }

    public LearningTagSubCategories buildSubCategories(LearningTagTaxonomy taxonomy, HyperlinkSearchResults hyperlinkSearchResults) {
        LearningTagSubCategories learningTagSubCategories = buildSubCategories(taxonomy);
        PagedResults<HyperlinkDto> links = PagedResults.fromSearchResults(hyperlinkSearchResults);
        learningTagSubCategories.setLinks(links);
        return learningTagSubCategories;
    }
}
