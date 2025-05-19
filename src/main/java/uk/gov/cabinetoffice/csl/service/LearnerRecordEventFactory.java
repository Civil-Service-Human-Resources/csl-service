package uk.gov.cabinetoffice.csl.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ITypedLearnerRecordResourceID;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEventDto;
import uk.gov.cabinetoffice.csl.util.IUtilService;

import java.time.LocalDateTime;

@Service
public class LearnerRecordEventFactory {

    private final String eventSourceUid;
    private final IUtilService utilService;

    public LearnerRecordEventFactory(@Value("${learnerRecord.record.sourceId}") String eventSourceUid,
                                     IUtilService utilService) {
        this.eventSourceUid = eventSourceUid;
        this.utilService = utilService;
    }

    public LearnerRecordEventDto createLearnerRecordEventDto(ITypedLearnerRecordResourceID recordResourceId, ILearnerRecordActionType type) {
        return createLearnerRecordEventDto(recordResourceId.getResourceId(), recordResourceId.getLearnerId(), type, utilService.getNowDateTime());
    }

    public LearnerRecordEventDto createLearnerRecordEventDto(String resourceId, String learnerId, ILearnerRecordActionType type, LocalDateTime timestamp) {
        return new LearnerRecordEventDto(resourceId, learnerId, type, eventSourceUid, timestamp, true);
    }

}
