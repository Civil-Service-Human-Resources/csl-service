package uk.gov.cabinetoffice.csl.controller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {
    private String message;
    private String courseTitle;
    private String courseId;

    public static CourseResponse fromMetaData(CourseRecordAction actionType, Course course) {
        return new CourseResponse(String.format("Successfully applied action '%s' to course record", actionType.getDescription()), course.getTitle(), course.getId());
    }
}
