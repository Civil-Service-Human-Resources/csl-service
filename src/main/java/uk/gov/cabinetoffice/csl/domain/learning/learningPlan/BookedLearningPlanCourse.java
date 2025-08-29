package uk.gov.cabinetoffice.csl.domain.learning.learningPlan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookedLearningPlanCourse extends LearningPlanCourse {

    private EventModule eventModule;
    private boolean canBeMovedToLearningRecord;

    public BookedLearningPlanCourse(String id, String title, String shortDescription, String type, Integer duration,
                                    Integer moduleCount, Integer costInPounds, State status, EventModule eventModule,
                                    boolean canBeMovedToLearningRecord) {
        super(id, title, shortDescription, type, duration, moduleCount, costInPounds, status);
        this.eventModule = eventModule;
        this.canBeMovedToLearningRecord = canBeMovedToLearningRecord;
    }
}
