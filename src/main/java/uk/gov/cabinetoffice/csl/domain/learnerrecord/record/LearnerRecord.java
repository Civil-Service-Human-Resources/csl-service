package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.LearningResourceType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ITypedLearnerRecordResourceID;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.TypedLearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.util.Cacheable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LearnerRecord implements Cacheable {
    private Long id;
    private LearnerRecordType recordType;
    private String resourceId;
    private String learnerId;
    private Long parentId;
    private LocalDateTime createdTimestamp;
    private List<LearnerRecordEvent> events;
    private Integer eventCount;
    private LearnerRecordEvent latestEvent;

    @JsonIgnore
    public String getCacheableId() {
        return getLearnerRecordId().getAsString();
    }

    @JsonIgnore
    public ITypedLearnerRecordResourceID getLearnerRecordId() {
        return new TypedLearnerRecordResourceId(getLearnerId(), getResourceId(), getType());
    }

    public LearningResourceType getType() {
        return recordType.getResourceType();
    }

    public List<LearnerRecordEvent> getEvents() {
        return events == null ? new ArrayList<>() : events;
    }
}
