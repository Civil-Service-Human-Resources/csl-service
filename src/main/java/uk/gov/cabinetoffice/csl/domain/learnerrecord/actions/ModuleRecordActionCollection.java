package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecordResourceId;

import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class ModuleRecordActionCollection {

    private final List<IModuleRecordAction> actions;
    private final List<ModuleRecordResourceId> moduleRecordIds;

    public static ModuleRecordActionCollection createWithSingleAction(IModuleRecordAction action) {
        return new ModuleRecordActionCollection(List.of(action), List.of(action.getModuleRecordId()));
    }

    public ModuleRecordActionCollectionResult process(Map<String, ModuleRecord> moduleRecordMap) {
        ModuleRecordActionCollectionResult result = new ModuleRecordActionCollectionResult();
        actions.forEach(action -> {
            ModuleRecord moduleRecord = moduleRecordMap.get(action.getModuleRecordId().getAsString());
            if (moduleRecord == null) {
                result.getNewRecords().add(action.generateNewModuleRecord());
            } else {
                result.getUpdatedRecords().add(action.applyUpdatesToModuleRecord(moduleRecord));
            }
        });
        return result;
    }
}
