package uk.gov.cabinetoffice.csl.controller.learning.model;

import lombok.Getter;

@Getter
public enum CategoryContent {

    COURSE("COURSE"),
    LINK("LINK");

    private final String name;

    CategoryContent(String name) {
        this.name = name;
    }
}
