package uk.gov.cabinetoffice.csl.domain.learnerrecord.ID;

import lombok.Getter;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.LearningResourceType;

@Getter
@Setter
public class TypedLearnerRecordResourceId extends LearnerRecordResourceId implements ITypedLearnerRecordResourceID {

    private final LearningResourceType type;

    public TypedLearnerRecordResourceId(String learnerId, String resourceId, LearningResourceType type) {
        super(learnerId, resourceId);
        this.type = type;
    }

}
