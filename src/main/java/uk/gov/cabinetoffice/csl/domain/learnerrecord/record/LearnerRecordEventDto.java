package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.cabinetoffice.csl.domain.LearningResourceType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.LearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LearnerRecordEventDto {
    private String resourceId;
    private String learnerId;
    private ILearnerRecordActionType actionType;
    private String eventSource;
    private LocalDateTime eventTimestamp;
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
    public LearnerRecordResourceId getResourceId() {
        return new LearnerRecordResourceId(getResourceType(), learnerId, resourceId);
    }
}
