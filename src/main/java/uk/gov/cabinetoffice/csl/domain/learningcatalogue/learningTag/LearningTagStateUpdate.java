package uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag;

import lombok.Getter;

@Getter
public enum LearningTagStateUpdate {

    ARCHIVE("ARCHIVE"),
    UNARCHIVE("UNARCHIVE");

    private final String name;

    LearningTagStateUpdate(String name) {
        this.name = name;
    }
}
