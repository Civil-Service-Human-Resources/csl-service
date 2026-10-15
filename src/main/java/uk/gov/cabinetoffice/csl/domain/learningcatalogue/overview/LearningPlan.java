package uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview;

import lombok.Getter;

@Getter
public enum LearningPlan {
    IS_IN_LEARNING_PLAN("IS_IN_LEARNING_PLAN"),
    NOT_IN_LEARNING_PLAN("NOT_IN_LEARNING_PLAN"),
    CANNOT_BE_ADDED_TO_LEARNING_PLAN("CANNOT_BE_ADDED_TO_LEARNING_PLAN");

    private final String name;

    LearningPlan(String name) {
        this.name = name;
    }
}
