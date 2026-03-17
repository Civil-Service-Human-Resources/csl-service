package uk.gov.cabinetoffice.csl.controller.model;

import lombok.Data;

@Data
public class UserLearningCourse {
    private String resourceId;
    private String title;
    private String status;
    private String completionDate;
}
