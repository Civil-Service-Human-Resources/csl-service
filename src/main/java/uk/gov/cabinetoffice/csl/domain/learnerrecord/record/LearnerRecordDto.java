package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.cabinetoffice.csl.domain.LearningResourceType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.LearnerRecordResourceId;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class LearnerRecordDto {

    private String recordType;
    private String resourceId;
    private String learnerId;
    private LocalDateTime createdTimestamp;
    private List<LearnerRecordEventDto> events;

    public LearnerRecordDto(String recordType, String resourceId, String learnerId, List<LearnerRecordEventDto> events) {
        this.recordType = recordType;
        this.resourceId = resourceId;
        this.learnerId = learnerId;
        this.events = events;
    }

    @JsonIgnore
    public LearningResourceType getResourceType() {
        return LearningResourceType.valueOf(recordType);
    }

    @JsonIgnore
    public LearnerRecordResourceId getResourceId() {
        return new LearnerRecordResourceId(getResourceType(), learnerId, resourceId);
    }

}
