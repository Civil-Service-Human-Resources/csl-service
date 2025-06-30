package uk.gov.cabinetoffice.csl.controller.model;

import uk.gov.cabinetoffice.csl.domain.LearningResourceType;

public class CourseResponse extends ResourceActionResponse {

    public CourseResponse(String actionDescription, String resourceId, String resourceTitle, boolean performedUpdate) {
        super(actionDescription, LearningResourceType.COURSE, resourceId, resourceTitle, performedUpdate);
    }

    public String getCourseTitle() {
        return this.getResourceTitle();
    }

    public String getCourseId() {
        return this.getResourceId();
    }
}
