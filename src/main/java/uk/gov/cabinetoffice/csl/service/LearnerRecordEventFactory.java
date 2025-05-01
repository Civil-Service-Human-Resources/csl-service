package uk.gov.cabinetoffice.csl.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.LearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEventDto;
import uk.gov.cabinetoffice.csl.util.IUtilService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LearnerRecordEventFactory {

    private final String eventSourceUid;
    private final IUtilService utilService;

    public LearnerRecordEventFactory(@Value("${learnerRecord.record.sourceId}") String eventSourceUid,
                                     IUtilService utilService) {
        this.eventSourceUid = eventSourceUid;
        this.utilService = utilService;
    }

    public List<LearnerRecordEventDto> createLearnerRecordEventDtos(LearnerRecordResourceId recordResourceId, List<ILearnerRecordActionType> types) {
        return types.stream().map(t -> createLearnerRecordEventDto(recordResourceId, t)).toList();
    }

    public LearnerRecordEventDto createLearnerRecordEventDto(LearnerRecordResourceId recordResourceId, ILearnerRecordActionType type) {
        return createLearnerRecordEventDto(recordResourceId.resourceId(), recordResourceId.learnerId(), type, utilService.getNowDateTime());
    }

    public LearnerRecordEventDto createLearnerRecordEventDto(String resourceId, String learnerId, ILearnerRecordActionType type, LocalDateTime timestamp) {
        return new LearnerRecordEventDto(resourceId, learnerId, type, eventSourceUid, timestamp, true);
    }

}
