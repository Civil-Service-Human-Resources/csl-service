package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import lombok.Getter;

@Getter
public enum CourseStatus {

    PUBLISHED("Published"),
    ARCHIVED("Archived");

    private final String name;

    CourseStatus(String name) {
        this.name = name;
    }
}
