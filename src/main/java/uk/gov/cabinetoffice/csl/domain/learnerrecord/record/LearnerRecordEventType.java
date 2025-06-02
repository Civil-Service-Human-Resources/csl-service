package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class LearnerRecordEventType implements Serializable {

    private final String eventType;
    private final Integer id;
    private final LearnerRecordType learnerRecordType;

}
