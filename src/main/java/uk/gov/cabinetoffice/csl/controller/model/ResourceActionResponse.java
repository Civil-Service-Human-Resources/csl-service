package uk.gov.cabinetoffice.csl.controller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.cabinetoffice.csl.domain.LearningResourceType;

@Data
@AllArgsConstructor
public class ResourceActionResponse {
    private String actionDescription;
    private LearningResourceType recordType;
    private String resourceId;
    private String resourceTitle;
    private boolean performedUpdate;

    private String getIdWithTitle() {
        return String.format("%s (%s)", resourceId, resourceTitle);
    }

    public String getMessage() {
        String message = performedUpdate ? "Successfully applied action '%s' to %s %s" : "Did not apply action '%s' to %s";
        return String.format(message, actionDescription, recordType, getIdWithTitle());
    }
}
