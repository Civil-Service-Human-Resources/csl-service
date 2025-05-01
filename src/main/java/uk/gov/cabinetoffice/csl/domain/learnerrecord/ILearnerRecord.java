package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import uk.gov.cabinetoffice.csl.domain.LearningResourceType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEvent;
import uk.gov.cabinetoffice.csl.util.Cacheable;

import java.time.LocalDateTime;

public interface ILearnerRecord extends Cacheable {

    Long getId();

    String getLearnerId();

    String getResourceId();

    LearningResourceType getType();

    LocalDateTime getCreatedTimestamp();

    LearnerRecordEvent getLatestEvent();

    default LearnerRecordResourceId getLearnerRecordId() {
        return new LearnerRecordResourceId(getType(), getLearnerId(), getResourceId());
    }

}
