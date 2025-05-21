package uk.gov.cabinetoffice.csl.service;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ITypedLearnerRecordResourceID;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.*;
import uk.gov.cabinetoffice.csl.util.IUtilService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LearnerRecordDtoFactory {

    private final IUtilService utilService;
    private final LearnerRecordEventFactory learnerRecordEventFactory;

    public LearnerRecordDtoFactory(IUtilService utilService, LearnerRecordEventFactory learnerRecordEventFactory) {
        this.utilService = utilService;
        this.learnerRecordEventFactory = learnerRecordEventFactory;
    }

    public LearnerRecordDto createLearnerRecordDto(LearnerRecordData data) {
        return createLearnerRecordDto(data.getResourceId(), data.getEvents().stream().map(LearnerRecordEventData::getActionType).toList());
    }

    public LearnerRecordDto createLearnerRecordDto(ITypedLearnerRecordResourceID id, List<ILearnerRecordActionType> events) {
        return createLearnerRecordDto(id, utilService.getNowDateTime(), events);
    }

    public LearnerRecordDto createLearnerRecordDto(ITypedLearnerRecordResourceID id,
                                                   LocalDateTime timestamp, List<ILearnerRecordActionType> events) {
        List<LearnerRecordEventDto> eventDtos = events.stream()
                .map(e -> learnerRecordEventFactory.createLearnerRecordEventDto(id.getResourceId(), id.getLearnerId(), e, timestamp)).toList();
        return new LearnerRecordDto(id.getType().name(), id.getResourceId(), id.getLearnerId(), timestamp, eventDtos);
    }

    public LearnerRecordDtoCollection createDtosFromData(List<LearnerRecordData> learnerRecordData) {
        List<LearnerRecordDto> newDtos = new ArrayList<>();
        List<LearnerRecordEventDto> newEventDtos = new ArrayList<>();
        for (LearnerRecordData learnerRecord : learnerRecordData) {
            if (learnerRecord.isNewRecord()) {
                newDtos.add(createLearnerRecordDto(learnerRecord));
            } else {
                newEventDtos.addAll(learnerRecord.getEvents().stream().filter(LearnerRecordEventData::isNewEvent).map(e -> this.learnerRecordEventFactory.createLearnerRecordEventDto(learnerRecord.getResourceId(), e.getActionType())).toList());
            }
        }
        return new LearnerRecordDtoCollection(newDtos, newEventDtos);
    }
}
