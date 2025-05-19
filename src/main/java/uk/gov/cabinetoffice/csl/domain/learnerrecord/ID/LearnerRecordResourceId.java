package uk.gov.cabinetoffice.csl.domain.learnerrecord.ID;

import lombok.Data;

@Data
public class LearnerRecordResourceId implements ILearnerRecordResourceID {
    private final String learnerId;
    private final String resourceId;

    public String getAsString() {
        return String.format("%s,%s", learnerId, resourceId);
    }

    public String toString() {
        return this.getAsString();
    }
    
}
