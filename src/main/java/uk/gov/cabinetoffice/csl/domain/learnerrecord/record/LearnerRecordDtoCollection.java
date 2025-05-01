package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.Data;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.LearnerRecordResourceId;

import java.util.List;
import java.util.stream.Stream;

@Data
public class LearnerRecordDtoCollection {

    private final List<LearnerRecordDto> newRecords;
    private final List<LearnerRecordEventDto> newEvents;

    public List<LearnerRecordResourceId> getResultIds() {
        return Stream.concat(newRecords.stream().map(LearnerRecordDto::getResourceId),
                newEvents.stream().map(LearnerRecordEventDto::getResourceId)).toList();
    }

    public boolean containsId(LearnerRecordResourceId id) {
        return getResultIds().stream().filter(recordResourceId -> recordResourceId.equals(id)).count() == 1;
    }

}
