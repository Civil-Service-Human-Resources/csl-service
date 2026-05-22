package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import lombok.Getter;

@Getter
public enum CourseVisibility {
    PUBLIC("PUBLIC"),
    PRIVATE("PRIVATE");

    private final String name;

    CourseVisibility(String name) {
        this.name = name;
    }
}
