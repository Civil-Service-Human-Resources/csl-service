package uk.gov.cabinetoffice.csl.service.job.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FailedMessagesResult {

    private Integer recordsAttempted;
    private Integer recordsProcessed;

    @Override
    public String toString() {
        return "FailedMessagesResult{" +
                "recordsAttempted=" + recordsAttempted +
                ", recordsProcessed=" + recordsProcessed +
                '}';
    }
}
