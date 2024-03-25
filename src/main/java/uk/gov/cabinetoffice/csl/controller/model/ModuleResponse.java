package uk.gov.cabinetoffice.csl.controller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module.ModuleRecordAction;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleResponse {
    private String message;
    private String courseTitle;
    private String moduleTitle;
    private String courseId;
    private String moduleId;

    public static ModuleResponse fromMetada(ModuleRecordAction actionType, CourseWithModule courseWithModule) {
        return new ModuleResponse(String.format("Successfully applied action '%s' to course record", actionType.getDescription()), courseWithModule.getCourse().getTitle(),
                courseWithModule.getModule().getTitle(), courseWithModule.getCourse().getId(), courseWithModule.getModule().getId());
    }
}
