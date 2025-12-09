package uk.gov.cabinetoffice.csl.service.learning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayModule;
import uk.gov.cabinetoffice.csl.domain.learning.learningRecord.LearningRecordCourse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.LearningPeriod;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@Service
@Slf4j
public class DisplayModuleFactory {

    public DisplayModule generateDisplayModule(Module module, @Nullable ModuleRecord moduleRecord, @Nullable LearningPeriod learningPeriod) {
        if (moduleRecord == null || learningPeriod == null) {
            return generateDisplayModule(module);
        }
        return new DisplayModule(module.getId(), module.getTitle(), module.getDescription(), module.getModuleType().getName(), !module.isOptional(),
                module.isRequiredForCompletion(), moduleRecord.getUpdatedAt(), moduleRecord.getCompletionDate(), moduleRecord.getStateForLearningPeriod(learningPeriod));
    }

    public DisplayModule generateDisplayModule(Module module) {
        return new DisplayModule(module.getId(), module.getTitle(), module.getDescription(), module.getModuleType().getName(), !module.isOptional(),
                module.isRequiredForCompletion(), null, null, State.NULL);
    }

    public DisplayModuleSummary generateDisplayModuleSummary(Collection<DisplayModule> modules, LearningRecordCourse learningRecordCourse) {
        int inProgressCount = 0;
        int requiredCompletedCount = 0;
        int requiredForCompletionCount = 0;
        for (DisplayModule displayModule : modules) {
            if (displayModule.isRequiredForCompletion()) {
                requiredForCompletionCount++;
                if (displayModule.getStatus().equals(State.COMPLETED)) {
                    requiredCompletedCount++;
                } else if (displayModule.getStatus().equals(State.IN_PROGRESS)) {
                    inProgressCount++;
                }
            } else {
                if (!displayModule.getStatus().equals(State.NULL)) {
                    inProgressCount++;
                }
            }
        }

        LocalDateTime completionDate = null;
        State state = (inProgressCount + requiredCompletedCount) == 0 ? State.NULL : State.IN_PROGRESS;
        if (learningRecordCourse != null) {
            completionDate = learningRecordCourse.getCompletionDate();
            if(completionDate != null) {
                state = State.COMPLETED;
            }
        }

        return new DisplayModuleSummary(completionDate, inProgressCount, requiredCompletedCount, requiredForCompletionCount, state);
    }

}
