package uk.gov.cabinetoffice.csl.controller.learning.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@NoArgsConstructor
@Getter
@Setter
public class LearningTagSubCategories extends LearningTagCategories {

    private String title;
    private String description;
    private Collection<Link> parents;

    public LearningTagSubCategories(Collection<LearningTagCategory> categories, String title, String description, Collection<Link> parents) {
        super(categories);
        this.title = title;
        this.description = description;
        this.parents = parents;
    }
}
