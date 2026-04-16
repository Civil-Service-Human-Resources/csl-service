package uk.gov.cabinetoffice.csl.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@AllArgsConstructor
@Getter
public class UserLearningResponse {
    private Collection<UserLearningCourse> learning;
    private int page;
    private int size;
    private long totalResults;
}
