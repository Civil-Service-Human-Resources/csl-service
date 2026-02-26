package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@AllArgsConstructor
public class LearnerRecordSearch {

    private LocalDateTime createdTimestampGte;
    private LocalDateTime updatedTimestampGte;
    private Collection<String> learnerIds;
    private Collection<String> eventTypes;

}
