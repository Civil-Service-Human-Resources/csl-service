package uk.gov.cabinetoffice.csl.service.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCategories;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagSubCategories;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learning.LearningTagTaxonomy;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseLearningTagSearchResults;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.HyperlinkDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.HyperlinkSearchResults;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTag;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningTagMapService;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningCategoryServiceTest {

    @Mock
    private LearningTagMapService learningTagMapService;

    @Mock
    private LearningCategoryFactory learningCategoryFactory;

    @Mock
    private CourseStatusService courseStatusService;

    @InjectMocks
    private LearningCategoryService learningCategoryService;

    private LearningTag tag;
    private LearningTagTaxonomy taxonomy;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        tag = new LearningTag();
        tag.setId(10L);
        tag.setName("Test Category");
        tag.setUrlSlug("test-category");
        tag.setCategory(true);
        tag.setArchived(false);
        tag.setCourseCount(0);
        tag.setLinkCount(0);

        taxonomy = new LearningTagTaxonomy(tag, new LinkedList<>(), List.of());
        pageable = PageRequest.of(0, 20);
    }

    @Test
    void testGetCategoriesRetrievesTierOneTaxonomiesAndBuildsCategories() {
        Collection<LearningTagTaxonomy> taxonomies = List.of(taxonomy);
        LearningTagCategories expectedCategories = new LearningTagCategories(List.of());

        when(learningTagMapService.getTierOneUnarchivedHomepageTaxonomies()).thenReturn(taxonomies);
        when(learningCategoryFactory.buildCategories(taxonomies)).thenReturn(expectedCategories);

        LearningTagCategories result = learningCategoryService.getCategories();

        assertNotNull(result);
        assertEquals(expectedCategories, result);
        verify(learningTagMapService).getTierOneUnarchivedHomepageTaxonomies();
        verify(learningCategoryFactory).buildCategories(taxonomies);
    }

    @Test
    void testGetCategoriesWithUrlWhenHasCourses() {
        tag.setCourseCount(2);
        CourseDto course = new CourseDto();
        course.setId("c1");
        CourseLearningTagSearchResults coursesResult = new CourseLearningTagSearchResults();
        coursesResult.setResults(List.of(course));
        coursesResult.setPage(0);
        coursesResult.setSize(20);
        coursesResult.setTotalResults(1);
        Map<String, State> states = Map.of("c1", State.APPROVED);
        LearningTagSubCategories expectedSubCategories = new LearningTagSubCategories();

        when(learningTagMapService.getUnarchivedHomepageTagsWithUrl("test-category")).thenReturn(taxonomy);
        when(learningTagMapService.getCourses(10L, 0, 20)).thenReturn(coursesResult);
        when(courseStatusService.getStateForCourses("user123", List.of("c1"))).thenReturn(states);
        when(learningCategoryFactory.buildSubCategories(taxonomy, coursesResult, states)).thenReturn(expectedSubCategories);

        LearningTagSubCategories result = learningCategoryService.getCategories("user123", "test-category", pageable);

        assertNotNull(result);
        assertEquals(expectedSubCategories, result);
        verify(learningTagMapService).getCourses(10L, 0, 20);
        verify(courseStatusService).getStateForCourses("user123", List.of("c1"));
        verify(learningCategoryFactory).buildSubCategories(taxonomy, coursesResult, states);
    }

    @Test
    void testGetCategoriesWithUrlWhenHasNoCoursesButHasLinks() {
        tag.setCourseCount(0);
        tag.setLinkCount(3);
        HyperlinkDto link = new HyperlinkDto(1L, "Title", "Desc", "https://link.com");
        HyperlinkSearchResults linksResult = new HyperlinkSearchResults();
        linksResult.setResults(List.of(link));
        linksResult.setPage(0);
        linksResult.setSize(20);
        linksResult.setTotalResults(1);
        LearningTagSubCategories expectedSubCategories = new LearningTagSubCategories();

        when(learningTagMapService.getUnarchivedHomepageTagsWithUrl("test-category")).thenReturn(taxonomy);
        when(learningTagMapService.getHyperlinks(10L, 0, 20)).thenReturn(linksResult);
        when(learningCategoryFactory.buildSubCategories(taxonomy, linksResult)).thenReturn(expectedSubCategories);

        LearningTagSubCategories result = learningCategoryService.getCategories("user123", "test-category", pageable);

        assertNotNull(result);
        assertEquals(expectedSubCategories, result);
        verify(learningTagMapService).getHyperlinks(10L, 0, 20);
        verify(learningCategoryFactory).buildSubCategories(taxonomy, linksResult);
    }

    @Test
    void testGetCategoriesWithUrlWhenNoDirectContent() {
        tag.setCourseCount(0);
        tag.setLinkCount(0);
        LearningTagSubCategories expectedSubCategories = new LearningTagSubCategories();

        when(learningTagMapService.getUnarchivedHomepageTagsWithUrl("test-category")).thenReturn(taxonomy);
        when(learningCategoryFactory.buildSubCategories(taxonomy)).thenReturn(expectedSubCategories);

        LearningTagSubCategories result = learningCategoryService.getCategories("user123", "test-category", pageable);

        assertNotNull(result);
        assertEquals(expectedSubCategories, result);
        verify(learningCategoryFactory).buildSubCategories(taxonomy);
        verifyNoInteractions(courseStatusService);
    }

    @Test
    void testGetCategoriesCoursesWhenCourseCountZero() {
        tag.setCourseCount(0);
        LearningTagSubCategories expectedSubCategories = new LearningTagSubCategories();

        when(learningTagMapService.getUnarchivedHomepageTagsWithUrl("test-category")).thenReturn(taxonomy);
        when(learningCategoryFactory.buildSubCategories(taxonomy)).thenReturn(expectedSubCategories);

        LearningTagSubCategories result = learningCategoryService.getCategoriesCourses("user123", "test-category", pageable);

        assertNotNull(result);
        assertEquals(expectedSubCategories, result);
        verify(learningCategoryFactory).buildSubCategories(taxonomy);
        verifyNoInteractions(courseStatusService);
    }

    @Test
    void testGetCategoriesHyperlinksWhenLinkCountZero() {
        tag.setLinkCount(0);
        LearningTagSubCategories expectedSubCategories = new LearningTagSubCategories();

        when(learningTagMapService.getUnarchivedHomepageTagsWithUrl("test-category")).thenReturn(taxonomy);
        when(learningCategoryFactory.buildSubCategories(taxonomy)).thenReturn(expectedSubCategories);

        LearningTagSubCategories result = learningCategoryService.getCategoriesHyperlinks("test-category", pageable);

        assertNotNull(result);
        assertEquals(expectedSubCategories, result);
        verify(learningCategoryFactory).buildSubCategories(taxonomy);
    }
}
