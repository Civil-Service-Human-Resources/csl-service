package uk.gov.cabinetoffice.csl.service.learningResources;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.ILearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.LearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordData;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordDtoCollection;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordResults;

import java.util.List;
import java.util.Map;

public interface ILearnerRecordService<R extends ILearnerRecord> {
    R getLearnerRecord(LearnerRecordResourceId id);

    List<R> getLearnerRecords(List<LearnerRecordResourceId> ids);

    LearnerRecordData getLearnerRecordAsData(LearnerRecordResourceId id);

    Map<String, LearnerRecordData> getLearnerRecordsAsData(List<LearnerRecordResourceId> ids);

    List<R> getLearnerRecords(String learnerId);

    LearnerRecordDtoCollection processLearnerRecordUpdates(List<LearnerRecordData> learnerRecordData);

    LearnerRecordDtoCollection processUpdates(LearnerRecordResults learnerRecordUpdates);

    void bustLearnerRecordCache(String learnerId, String userId);
}
