package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.LearningResourceType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LearnerRecordType {
    private Integer id;
    private String type;

    public LearningResourceType getResourceType() {
        return LearningResourceType.valueOf(type);
    }
}
