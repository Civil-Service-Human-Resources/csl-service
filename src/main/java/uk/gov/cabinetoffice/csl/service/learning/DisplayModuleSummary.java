package uk.gov.cabinetoffice.csl.service.learning;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class DisplayModuleSummary {

    private LocalDateTime completionDate;
    private int inProgressCount = 0;
    private int requiredCompletedCount = 0;
    private int requiredForCompletionCount = 0;
    private State status;

    public LocalDateTime getCompletionDate() {
        return getStatus() == State.COMPLETED ? completionDate : null;
    }

}
