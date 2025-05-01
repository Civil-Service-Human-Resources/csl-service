package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseRecordAction implements ICourseRecordActionType {
    ADD_TO_LEARNING_PLAN("ADD_TO_LEARNING_PLAN", "Add to learning plan", true, false),
    REMOVE_FROM_LEARNING_PLAN("REMOVE_FROM_LEARNING_PLAN", "Remove from learning plan", false, false),
    REMOVE_FROM_SUGGESTIONS("REMOVE_FROM_SUGGESTIONS", "Remove from suggestions", true, false),
    COMPLETE_COURSE("COMPLETE_COURSE", "Complete a course", true, true);

    private final String name;
    private final String description;
    private final boolean canCreateRecord;
    private final boolean canRepeat;
    
    public boolean canCreateRecord() {
        return canCreateRecord;
    }

    public boolean canRepeat() {
        return canRepeat;
    }
}
