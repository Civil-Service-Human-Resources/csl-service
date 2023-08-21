package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ModuleRecordInput {

    private String uid;
    private String userId;
    private String courseId;
    private String moduleId;
    private String moduleTitle;
    private Boolean optional;
    private Long duration;
    private String moduleType;
    private BigDecimal cost;
    private String state;
    private String result;
    private LocalDate eventDate;
    private String eventId;
    private LocalDateTime completedDate;

    public static ModuleRecordInput from(String learnerId, String courseId,
                                         Module module, ModuleRecordStatus status) {
        return new ModuleRecordInput(status.getUid(), learnerId, courseId,
                module.getId(), module.getTitle(), module.isOptional(),
                module.getDuration(), module.getModuleType(), module.getCost(),
                status.getState(), status.getResult(), status.getEventDate(), status.getEventId(),
                status.getCompletedDate());
    }
}
