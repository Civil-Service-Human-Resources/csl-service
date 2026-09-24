package uk.gov.cabinetoffice.csl.service.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCategories;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCategory;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagSubCategories;
import uk.gov.cabinetoffice.csl.controller.learning.model.Link;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learning.LearningTagTaxonomy;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseLearningTagSearchResults;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.HyperlinkDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.HyperlinkSearchResults;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTag;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LearningCategoryFactoryTest {

    private LearningCategoryFactory factory;

    @BeforeEach
    void setUp() {
        factory = new LearningCategoryFactory();
    }

    private LearningTag createTag(Long id, String name, String urlSlug, boolean isCategory, boolean isArchived, Integer courseCount, Integer linkCount) {
        LearningTag tag = new LearningTag();
        tag.setId(id);
        tag.setName(name);
        tag.setDescription(name + " description");
        tag.setUrlSlug(urlSlug);
        tag.setCategory(isCategory);
        tag.setArchived(isArchived);
        tag.setCourseCount(courseCount);
        tag.setLinkCount(linkCount);
        tag.setCreatedTimestamp(LocalDateTime.now());
        return tag;
    }

    @Test
    void testBuildCategoriesPopulatesChildLinksAndCountsSortedAlphabetically() {
        LearningTag tier1TagB = createTag(1L, "Beta Category", "beta-cat", true, false, 5, 2);
        LearningTag tier1TagA = createTag(2L, "Alpha Category", "alpha-cat", true, false, 0, 0);

        LearningTag childTagZ = createTag(3L, "Zebra Subject", "zebra-sub", true, false, 1, 0);
        LearningTag childTagM = createTag(4L, "Mango Subject", "mango-sub", true, false, 0, 1);
        LearningTag childTagArchived = createTag(5L, "Archived Subject", "archived-sub", true, true, 0, 0);
        LearningTag childTagNotCategory = createTag(6L, "Not Category", "not-cat", false, false, 0, 0);

        LearningTagTaxonomy childTaxZ = new LearningTagTaxonomy(childTagZ, new LinkedList<>(), List.of());
        LearningTagTaxonomy childTaxM = new LearningTagTaxonomy(childTagM, new LinkedList<>(), List.of());
        LearningTagTaxonomy childTaxArchived = new LearningTagTaxonomy(childTagArchived, new LinkedList<>(), List.of());
        LearningTagTaxonomy childTaxNotCategory = new LearningTagTaxonomy(childTagNotCategory, new LinkedList<>(), List.of());

        LearningTagTaxonomy taxB = new LearningTagTaxonomy(tier1TagB, new LinkedList<>(), List.of(childTaxZ, childTaxM, childTaxArchived, childTaxNotCategory));
        LearningTagTaxonomy taxA = new LearningTagTaxonomy(tier1TagA, new LinkedList<>(), List.of());

        LearningTagCategories result = factory.buildCategories(List.of(taxB, taxA));

        assertNotNull(result);
        List<LearningTagCategory> categories = new ArrayList<>(result.getCategories());
        assertEquals(2, categories.size());

        // Assert categories are sorted alphabetically by title
        LearningTagCategory firstCat = categories.get(0);
        assertEquals("Alpha Category", firstCat.getTitle());
        assertEquals("alpha-cat", firstCat.getUrl());
        assertEquals("Alpha Category description", firstCat.getDescription());
        assertEquals(0, firstCat.getCourseCount());
        assertEquals(0, firstCat.getLinkCount());
        assertTrue(firstCat.getCategories().isEmpty());

        LearningTagCategory secondCat = categories.get(1);
        assertEquals("Beta Category", secondCat.getTitle());
        assertEquals("beta-cat", secondCat.getUrl());
        assertEquals("Beta Category description", secondCat.getDescription());
        assertEquals(5, secondCat.getCourseCount());
        assertEquals(2, secondCat.getLinkCount());

        // Assert child links are filtered and sorted alphabetically by name (Link::getText)
        List<Link> childLinks = new ArrayList<>(secondCat.getCategories());
        assertEquals(2, childLinks.size());
        assertEquals("Mango Subject", childLinks.get(0).getText());
        assertEquals("mango-sub", childLinks.get(0).getLink());
        assertEquals("Zebra Subject", childLinks.get(1).getText());
        assertEquals("zebra-sub", childLinks.get(1).getLink());
    }

    @Test
    void testBuildSubCategories() {
        LearningTag parentTag = createTag(1L, "Parent Category", "parent-cat", true, false, 0, 0);
        LearningTag currentTag = createTag(2L, "Current Category", "current-cat", true, false, 3, 1);
        LearningTag childTag2 = createTag(3L, "Sub Beta", "sub-beta", true, false, 0, 0);
        LearningTag childTag1 = createTag(4L, "Sub Alpha", "sub-alpha", true, false, 0, 0);
        LearningTag grandchild = createTag(5L, "Grandchild", "grandchild", true, false, 0, 0);

        LearningTagTaxonomy grandchildTax = new LearningTagTaxonomy(grandchild, new LinkedList<>(), List.of());
        LearningTagTaxonomy childTax1 = new LearningTagTaxonomy(childTag1, new LinkedList<>(), List.of(grandchildTax));
        LearningTagTaxonomy childTax2 = new LearningTagTaxonomy(childTag2, new LinkedList<>(), List.of());

        LinkedList<LearningTag> parents = new LinkedList<>(List.of(parentTag));
        LearningTagTaxonomy taxonomy = new LearningTagTaxonomy(currentTag, parents, List.of(childTax2, childTax1));

        LearningTagSubCategories result = factory.buildSubCategories(taxonomy);

        assertNotNull(result);
        assertEquals("Current Category", result.getTitle());
        assertEquals("current-cat", result.getUrl());
        assertEquals("Current Category description", result.getDescription());
        assertEquals(3, result.getCourseCount());
        assertEquals(1, result.getLinkCount());

        List<Link> parentLinks = new ArrayList<>(result.getParents());
        assertEquals(1, parentLinks.size());
        assertEquals("Parent Category", parentLinks.get(0).getText());
        assertEquals("parent-cat", parentLinks.get(0).getLink());

        List<LearningTagCategory> subCategories = new ArrayList<>(result.getCategories());
        assertEquals(2, subCategories.size());
        assertEquals("Sub Alpha", subCategories.get(0).getTitle());
        assertEquals(1, subCategories.get(0).getCategories().size());
        assertEquals("Grandchild", subCategories.get(0).getCategories().iterator().next().getText());

        assertEquals("Sub Beta", subCategories.get(1).getTitle());
        assertTrue(subCategories.get(1).getCategories().isEmpty());
    }

    @Test
    void testBuildSubCategoriesWithCourses() {
        LearningTag currentTag = createTag(2L, "Current Category", "current-cat", true, false, 2, 0);
        LearningTagTaxonomy taxonomy = new LearningTagTaxonomy(currentTag, new LinkedList<>(), List.of());

        CourseDto course1 = new CourseDto();
        course1.setId("c1");
        course1.setTitle("Course 1");
        course1.setShortDescription("Desc 1");

        CourseLearningTagSearchResults searchResults = new CourseLearningTagSearchResults();
        searchResults.setResults(List.of(course1));
        searchResults.setPage(0);
        searchResults.setSize(10);
        searchResults.setTotalResults(1);
        Map<String, State> states = Map.of("c1", State.IN_PROGRESS);

        LearningTagSubCategories result = factory.buildSubCategories(taxonomy, searchResults, states);

        assertNotNull(result);
        assertEquals(1, result.getCourses().getResults().size());
        assertEquals("c1", result.getCourses().getResults().get(0).getId());
        assertEquals("Course 1", result.getCourses().getResults().get(0).getTitle());
        assertEquals(State.IN_PROGRESS, result.getCourses().getResults().get(0).getStatus());
    }

    @Test
    void testBuildSubCategoriesWithHyperlinks() {
        LearningTag currentTag = createTag(2L, "Current Category", "current-cat", true, false, 0, 1);
        LearningTagTaxonomy taxonomy = new LearningTagTaxonomy(currentTag, new LinkedList<>(), List.of());

        HyperlinkDto hyperlink = new HyperlinkDto(1L, "Hyperlink 1", "Desc 1", "https://example.com");
        HyperlinkSearchResults searchResults = new HyperlinkSearchResults();
        searchResults.setResults(List.of(hyperlink));
        searchResults.setPage(0);
        searchResults.setSize(10);
        searchResults.setTotalResults(1);

        LearningTagSubCategories result = factory.buildSubCategories(taxonomy, searchResults);

        assertNotNull(result);
        assertEquals(1, result.getLinks().getResults().size());
        assertEquals(1L, result.getLinks().getResults().get(0).getId());
        assertEquals("Hyperlink 1", result.getLinks().getResults().get(0).getTitle());
    }
}
