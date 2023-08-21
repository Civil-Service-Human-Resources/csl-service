package uk.gov.cabinetoffice.csl.controller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveCourseFromSuggestionsResponse {
    private boolean success;
    private String message;
    private String courseTitle;
    private String courseId;
}
