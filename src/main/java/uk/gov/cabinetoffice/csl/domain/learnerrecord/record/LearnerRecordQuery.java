package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Builder
@Data
public class LearnerRecordQuery {
    private List<String> notEventTypes;
    private List<String> learnerRecordTypes;
    private Set<String> resourceIds;
    private Set<String> learnerIds;
    private String uid;
    private boolean getChildRecords;

}
