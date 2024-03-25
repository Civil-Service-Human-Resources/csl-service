package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordActionType;

public enum CourseRecordAction implements ICourseRecordActionType {
    ADD_TO_LEARNING_PLAN("Add to learning plan"),
    REMOVE_FROM_LEARNING_PLAN("Remove from learning plan"),
    REMOVE_FROM_SUGGESTIONS("Remove from suggestions");

    private final String description;

    CourseRecordAction(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
