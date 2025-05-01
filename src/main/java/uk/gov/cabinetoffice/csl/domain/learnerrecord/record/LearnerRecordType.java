package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LearnerRecordType {
    private Integer id;
    private String type;
}
