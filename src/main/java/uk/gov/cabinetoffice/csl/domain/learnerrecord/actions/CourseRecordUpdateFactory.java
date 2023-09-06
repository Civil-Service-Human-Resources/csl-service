package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.springframework.stereotype.Service;

@Service
public class CourseRecordUpdateFactory {

    public ICourseRecordUpdate getAddToLearningPlanUpdate() {
        return new AddToLearningPlanUpdate();
    }

    public ICourseRecordUpdate getRemoveFromLearningPlanUpdate() {
        return new RemoveFromLearningPlanUpdate();
    }

    public ICourseRecordUpdate getRemoveFromSuggestionsUpdate() {
        return new RemoveFromSuggestionsUpdate();
    }
}
