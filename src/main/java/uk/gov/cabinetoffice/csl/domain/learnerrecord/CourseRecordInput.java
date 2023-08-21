package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
public class CourseRecordInput {

    private String courseId;
    private String userId;
    private String courseTitle;
    private String state;
    private Boolean isRequired;
    private String preference;
    private List<ModuleRecordInput> moduleRecords;

    public static CourseRecordInput from(String learnerId, Course course,
                                         CourseRecordStatus status) {
        return new CourseRecordInput(
                course.getId(), learnerId, course.getTitle(),
                status.getState(), status.getIsRequired(),
                status.getPreference(), Collections.emptyList()
        );
    }

    public static CourseRecordInput from(String learnerId, Course course,
                                         CourseRecordStatus status,
                                         Module module, ModuleRecordStatus moduleRecordStatus) {
        ModuleRecordInput moduleRecordInput = ModuleRecordInput.from(
                learnerId, course.getId(), module, moduleRecordStatus
        );
        return new CourseRecordInput(
                course.getId(), learnerId, course.getTitle(),
                status.getState(), status.getIsRequired(),
                status.getPreference(), Collections.singletonList(moduleRecordInput)
        );
    }
}
