package uk.gov.cabinetoffice.csl.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;

@AllArgsConstructor
@Getter
@Setter
public class ModuleWithRecord implements ILearningResource {

    private final String learnerId;
    private final Module module;
    private final ModuleRecord record;

    public ModuleRecordResourceId getRecordResourceId() {
        return new ModuleRecordResourceId(learnerId, getResourceId());
    }

    @Override
    public String getResourceId() {
        return module.getResourceId();
    }

    @Override
    public String getName() {
        return module.getName();
    }

    @Override
    public LearningResourceType getType() {
        return LearningResourceType.MODULE;
    }
}
