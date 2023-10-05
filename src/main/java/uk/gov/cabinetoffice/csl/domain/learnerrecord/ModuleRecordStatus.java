package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ModuleRecordStatus {
    private String uid;
    private String state;
    private String result;
    private LocalDate eventDate;
    private String eventId;
    private LocalDateTime completedDate;
}
