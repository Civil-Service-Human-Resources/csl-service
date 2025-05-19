package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.LearningResourceType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ITypedLearnerRecordResourceID;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.TypedLearnerRecordResourceId;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LearnerRecordDto {

    private String recordType;
    private String resourceId;
    private String learnerId;
    private LocalDateTime createdTimestamp;
    private List<LearnerRecordEventDto> events;

    @JsonIgnore
    public LearningResourceType getResourceType() {
        return LearningResourceType.valueOf(recordType);
    }

    @JsonIgnore
    public ITypedLearnerRecordResourceID getLearnerRecordResourceId() {
        return new TypedLearnerRecordResourceId(learnerId, resourceId, getResourceType());
    }

}
