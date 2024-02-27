package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordActionType;

public enum EventModuleRecordAction implements ICourseRecordActionType {
    REGISTER_BOOKING("Register for an event"),
    CANCEL_BOOKING("Cancel a booking"),
    APPROVE_BOOKING("Approve a booking"),
    COMPLETE_BOOKING("Complete a booking"),
    SKIP_BOOKING("Skip a booking");

    private final String description;

    EventModuleRecordAction(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
