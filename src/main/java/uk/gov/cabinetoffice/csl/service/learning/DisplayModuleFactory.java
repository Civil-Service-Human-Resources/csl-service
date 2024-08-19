package uk.gov.cabinetoffice.csl.service.learning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.LearningPeriod;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;

@Service
@Slf4j
public class DisplayModuleFactory {

    public DisplayModule generateDisplayModule(Module module, ModuleRecord moduleRecord, LearningPeriod learningPeriod) {
        return new DisplayModule(module.getId(), module.getTitle(), module.getDescription(), module.getModuleType().getName(), !module.isOptional(),
                moduleRecord.getUpdatedAt(), moduleRecord.getCompletionDate(), moduleRecord.getStateForLearningPeriod(learningPeriod));
    }

    public DisplayModule generateDisplayModule(Module module) {
        return new DisplayModule(module.getId(), module.getTitle(), module.getDescription(), module.getModuleType().getName(), !module.isOptional(),
                null, null, State.NULL);
    }

}
