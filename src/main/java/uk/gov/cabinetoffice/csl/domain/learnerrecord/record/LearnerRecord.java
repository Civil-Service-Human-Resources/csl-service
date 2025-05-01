package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.LearningResourceType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ILearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;
import uk.gov.cabinetoffice.csl.util.Cacheable;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LearnerRecord implements Cacheable, ILearnerRecord {
    private Long id;
    private Integer recordType;
    private LearningResourceType learningResourceType;
    private String resourceId;
    private String learnerId;
    private Long parentId;
    private LocalDateTime createdTimestamp;
    private Integer eventCount;
    private LearnerRecordEvent latestEvent;

    @JsonIgnore
    public String getCacheableId() {
        return getLearnerRecordId().getAsString();
    }

    @JsonIgnore
    public boolean doesEventMatchMostRecentEvent(ILearnerRecordActionType learnerRecordAction) {
        return latestEvent != null && learnerRecordAction.getName().equals(latestEvent.getEventType().getEventType());
    }

    @Override
    public LearningResourceType getType() {
        return learningResourceType;
    }
}
