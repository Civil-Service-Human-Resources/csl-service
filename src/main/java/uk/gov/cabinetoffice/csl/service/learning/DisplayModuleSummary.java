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

    public LocalDateTime getCompletionDate() {
        return getStatus() == State.COMPLETED ? completionDate : null;
    }

    public State getStatus() {
        State status = State.NULL;
        if (requiredCompletedCount == requiredForCompletionCount) {
            status = State.COMPLETED;
        } else if (inProgressCount > 0 || requiredCompletedCount > 0) {
            status = State.IN_PROGRESS;
        }
        return status;
    }

}
