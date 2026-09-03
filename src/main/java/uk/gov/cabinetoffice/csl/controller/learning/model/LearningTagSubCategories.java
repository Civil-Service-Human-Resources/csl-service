package uk.gov.cabinetoffice.csl.controller.learning.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.controller.model.PagedResults;
import uk.gov.cabinetoffice.csl.domain.learning.learningPlan.BasicCourse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.HyperlinkDto;

import java.util.Collection;

@NoArgsConstructor
@Getter
@Setter
public class LearningTagSubCategories extends LearningTagCategories {

    private String title;
    private String description;
    private Collection<Link> parents;
    private Integer courseCount;
    private PagedResults<BasicCourse> courses;
    private Integer linkCount;
    private PagedResults<HyperlinkDto> links;

    public LearningTagSubCategories(Collection<LearningTagCategory> categories, String title, String description, Collection<Link> parents,
                                    Integer courseCount, PagedResults<BasicCourse> courses, Integer linkCount, PagedResults<HyperlinkDto> links) {
        super(categories);
        this.title = title;
        this.description = description;
        this.parents = parents;
        this.courseCount = courseCount;
        this.courses = courses;
        this.linkCount = linkCount;
        this.links = links;
    }
}
