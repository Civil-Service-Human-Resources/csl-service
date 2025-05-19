package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.cabinetoffice.csl.domain.LearningResourceType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ITypedLearnerRecordResourceID;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.TypedLearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LearnerRecordEventDto {
    private String resourceId;
    private String learnerId;
    @JsonIgnore
    private ILearnerRecordActionType actionType;
    private String eventSource;
    private LocalDateTime eventTimestamp;

    @JsonIgnore
    private boolean newEvent;

    @JsonProperty("eventType")
    public String getEventType() {
        return actionType.getName();
    }

    @JsonIgnore
    public LearningResourceType getResourceType() {
        return actionType.getRecordType();
    }

    @JsonIgnore
    public ITypedLearnerRecordResourceID getLearnerRecordResourceId() {
        return new TypedLearnerRecordResourceId(learnerId, resourceId, getResourceType());
    }
}
