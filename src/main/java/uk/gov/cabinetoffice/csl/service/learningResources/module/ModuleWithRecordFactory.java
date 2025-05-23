package uk.gov.cabinetoffice.csl.service.learningResources.module;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.ModuleWithRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;

@Service
public class ModuleWithRecordFactory {
    public ModuleWithRecord build(String learnerId, Module module, @Nullable ModuleRecord moduleRecord) {
        return new ModuleWithRecord(learnerId, module, moduleRecord);
    }

}
