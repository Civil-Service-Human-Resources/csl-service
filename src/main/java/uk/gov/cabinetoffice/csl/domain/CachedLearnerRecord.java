package uk.gov.cabinetoffice.csl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.ILearnerRecordEvent;
import uk.gov.cabinetoffice.csl.util.Cacheable;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CachedLearnerRecord implements Cacheable {

    private String resourceId;
    private String learnerId;
    private ILearnerRecordEvent mostRecentEvent;
    private LocalDateTime createdTimestamp;

    public CachedLearnerRecord(String resourceId, String learnerId, LocalDateTime createdTimestamp) {
        this.resourceId = resourceId;
        this.learnerId = learnerId;
        this.createdTimestamp = createdTimestamp;
    }

    @JsonIgnore
    public String getCacheableId() {
        return String.format("%s,%s", learnerId, resourceId);
    }
}
