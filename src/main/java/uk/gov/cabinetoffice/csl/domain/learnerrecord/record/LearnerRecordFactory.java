package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.LearningResourceType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;

@Service
@Slf4j
public class LearnerRecordFactory {

    public LearnerRecord transformLearnerRecord(LearnerRecord learnerRecord) {
        LearnerRecordEvent latestEvent = learnerRecord.getLatestEvent();
        if (latestEvent != null) {
            learnerRecord.setLatestEvent(applyLearnerRecordEventData(latestEvent));
        }
        return learnerRecord;
    }

    public LearnerRecordEvent applyLearnerRecordEventData(LearnerRecordEvent learnerRecordEvent) {
        LearnerRecordEventType eventType = learnerRecordEvent.getEventType();
        String eventTypeString = eventType.getEventType();
        LearningResourceType resourceType = eventType.getLearnerRecordType().getResourceType();
        ILearnerRecordActionType actionType = null;
        if (resourceType.equals(LearningResourceType.COURSE)) {
            try {
                actionType = CourseRecordAction.valueOf(eventTypeString);
            } catch (IllegalArgumentException e) {
                log.error(String.format("Attempted to case %s to course record action, which is invalid", eventTypeString));
            }
        }
        if (actionType == null) {
            throw new RuntimeException(String.format("Event type %s is not supported for resrouce type %s", eventTypeString, resourceType));
        }
        learnerRecordEvent.setActionType(actionType);
        return learnerRecordEvent;
    }

}
