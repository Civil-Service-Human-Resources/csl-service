package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.Data;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ILearnerRecordResourceID;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ITypedLearnerRecordResourceID;

import java.util.List;
import java.util.stream.Stream;

@Data
public class LearnerRecordDtoCollection {

    private final List<LearnerRecordDto> newRecords;
    private final List<LearnerRecordEventDto> newEvents;

    public List<ITypedLearnerRecordResourceID> getResultIds() {
        return Stream.concat(newRecords.stream().map(LearnerRecordDto::getLearnerRecordResourceId),
                newEvents.stream().map(LearnerRecordEventDto::getLearnerRecordResourceId)).toList();
    }

    public boolean containsId(ILearnerRecordResourceID id) {
        return getResultIds().stream().filter(recordResourceId -> recordResourceId.equals(id)).count() == 1;
    }

}
