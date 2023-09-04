package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.springframework.stereotype.Service;

@Service
public class CourseRecordActionService {

    public CourseRecordUpdate getAddToLearningPlanUpdate() {
        return new AddToLearningPlanUpdate();
    }

    public CourseRecordUpdate getRemoveFromLearningPlanUpdate() {
        return new RemoveFromLearningPlanUpdate();
    }

    public CourseRecordUpdate getRemoveFromSuggestionsUpdate() {
        return new RemoveFromSuggestionsUpdate();
    }
}
