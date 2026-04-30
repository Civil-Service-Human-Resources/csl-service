package uk.gov.cabinetoffice.csl.controller.model;

import lombok.Data;
import java.util.List;

@Data
public class UserLearningResponse {
    private List<UserLearningCourse> learning;
    private int page;
    private int size;
    private long totalResults;
}
