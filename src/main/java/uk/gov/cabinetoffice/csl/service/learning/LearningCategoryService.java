package uk.gov.cabinetoffice.csl.service.learning;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagCategories;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagSubCategories;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learning.LearningTagTaxonomy;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseLearningTagSearchResults;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.HyperlinkSearchResults;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTag;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningTagMapService;

import java.util.Collection;
import java.util.Map;

@Service
public class LearningCategoryService {

    private final LearningTagMapService learningTagMapService;
    private final LearningCategoryFactory learningCategoryFactory;
    private final CourseStatusService courseStatusService;


    public LearningCategoryService(LearningTagMapService learningTagMapService, LearningCategoryFactory learningCategoryFactory, CourseStatusService courseStatusService) {
        this.learningTagMapService = learningTagMapService;
        this.learningCategoryFactory = learningCategoryFactory;
        this.courseStatusService = courseStatusService;
    }

    public LearningTagCategories getCategories() {
        Collection<LearningTag> tags = learningTagMapService.getTierOneUnarchivedHomepageTags();
        return learningCategoryFactory.buildCategories(tags);
    }

    public LearningTagSubCategories getCategories(String uid, String urlSlug, Pageable pageableParams) {
        LearningTagTaxonomy taxonomy = learningTagMapService.getUnarchivedHomepageTagsWithUrl(urlSlug);
        if (taxonomy.category().getCourseCount() > 0) {
            return getCategoriesCourses(uid, taxonomy, pageableParams);
        } else if (taxonomy.category().getLinkCount() > 0) {
            return getCategoriesHyperlinks(taxonomy, pageableParams);
        } else {
            return learningCategoryFactory.buildSubCategories(taxonomy);
        }
    }

    public LearningTagSubCategories getCategoriesCourses(String uid, String urlSlug, Pageable pageableParams) {
        LearningTagTaxonomy taxonomy = learningTagMapService.getUnarchivedHomepageTagsWithUrl(urlSlug);
        if (taxonomy.category().getCourseCount() > 0) {
            return getCategoriesCourses(uid, taxonomy, pageableParams);
        } else {
            return learningCategoryFactory.buildSubCategories(taxonomy);
        }
    }

    LearningTagSubCategories getCategoriesCourses(String uid, LearningTagTaxonomy taxonomy, Pageable pageableParams) {
        CourseLearningTagSearchResults courses = learningTagMapService.getCourses(taxonomy.category().getId(), pageableParams.getPageNumber(), pageableParams.getPageSize());
        Map<String, State> courseStates = courseStatusService.getStateForCourses(uid, courses.getResults().stream().map(CourseDto::getId).toList());
        return learningCategoryFactory.buildSubCategories(taxonomy, courses, courseStates);
    }

    public LearningTagSubCategories getCategoriesHyperlinks(String urlSlug, Pageable pageableParams) {
        LearningTagTaxonomy taxonomy = learningTagMapService.getUnarchivedHomepageTagsWithUrl(urlSlug);
        if (taxonomy.category().getLinkCount() > 0) {
            return getCategoriesHyperlinks(taxonomy, pageableParams);
        } else {
            return learningCategoryFactory.buildSubCategories(taxonomy);
        }
    }

    LearningTagSubCategories getCategoriesHyperlinks(LearningTagTaxonomy taxonomy, Pageable pageableParams) {
        HyperlinkSearchResults links = learningTagMapService.getHyperlinks(taxonomy.category().getId(), pageableParams.getPageNumber(), pageableParams.getPageSize());
        return learningCategoryFactory.buildSubCategories(taxonomy, links);
    }
}
