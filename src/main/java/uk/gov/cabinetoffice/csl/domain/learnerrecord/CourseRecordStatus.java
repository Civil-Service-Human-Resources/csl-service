package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseRecordStatus {
    private String state;
    private Boolean isRequired;
    private String preference;
    private ModuleRecordStatus moduleRecordStatus;
}
