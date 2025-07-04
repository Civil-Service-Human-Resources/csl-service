package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class LearnerRecordEventQuery {
    List<String> eventTypes;
    Integer eventSource;
    String userId;
    LocalDateTime before;
    LocalDateTime after;
}
