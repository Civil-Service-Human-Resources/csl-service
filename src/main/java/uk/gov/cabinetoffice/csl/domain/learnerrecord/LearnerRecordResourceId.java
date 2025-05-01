package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import lombok.Data;
import uk.gov.cabinetoffice.csl.domain.LearningResourceType;

@Data
public class LearnerRecordResourceId {
    private final LearningResourceType type;
    private final String learnerId;
    private final String resourceId;

    public String getAsString() {
        return String.format("%s,%s", learnerId, resourceId);
    }

    public String toString() {
        return this.getAsString();
    }

    public boolean equals(LearnerRecordResourceId id) {
        return id.getAsString().equals(getAsString());
    }
}
