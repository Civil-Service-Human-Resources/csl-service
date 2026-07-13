package uk.gov.cabinetoffice.csl.controller.learning.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LearningTagOverview {

    private Long id;
    private String name;
    private String description;
    private String code;
    private String urlSlug;
    private String fullUrl;
    private Long parentId;
    private String parentName;
    private boolean category;
    private boolean archived;

}
