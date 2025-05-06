package uk.gov.cabinetoffice.csl.service.notification.messages;

import lombok.Getter;

@Getter
public enum EmailNotification {

    NOTIFY_LINE_MANAGER_COMPLETED_LEARNING("notifyLineManagerCompletedLearning"),
    NOTIFY_LEARNER_CANCELLED_EVENT("notifyLearnerEventCancelled");

    private final String configName;

    EmailNotification(String configName) {
        this.configName = configName;
    }
}
