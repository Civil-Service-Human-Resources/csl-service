package uk.gov.cabinetoffice.csl.service;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEvent;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEventQuery;

import java.util.HashMap;
import java.util.Map;

@Service
public class LearnerRecordDataUtils {

    private final LearnerRecordService learnerRecordService;

    public LearnerRecordDataUtils(LearnerRecordService learnerRecordService) {
        this.learnerRecordService = learnerRecordService;
    }

    public Map<String, LearnerRecordEvent> getLearnerRecordEventsNormalisedMostRecent(LearnerRecordEventQuery learnerRecordEventQuery) {
        Map<String, LearnerRecordEvent> eventMap = new HashMap<>();
        learnerRecordService.getLearnerRecordEvents(learnerRecordEventQuery)
                .forEach(e -> {
                    LearnerRecordEvent event = eventMap.get(e.getResourceId());
                    if (event == null) {
                        eventMap.put(e.getResourceId(), e);
                    } else {
                        if (e.getEventTimestamp().isAfter(event.getEventTimestamp())) {
                            eventMap.put(e.getResourceId(), e);
                        }
                    }
                });
        return eventMap;
    }

}
