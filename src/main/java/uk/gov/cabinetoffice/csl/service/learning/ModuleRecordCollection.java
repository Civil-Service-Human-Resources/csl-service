package uk.gov.cabinetoffice.csl.service.learning;

import lombok.Getter;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class ModuleRecordCollection extends ArrayList<ModuleRecord> {

    private ModuleRecord bookedEventModule = null;
    private List<String> completedModules = new ArrayList<>();
    private List<String> incompleteModules = new ArrayList<>();
    private LocalDateTime latestCompletionDate = LocalDateTime.MIN;
    private LocalDateTime latestUpdatedDate = LocalDateTime.MIN;

    public boolean isLastModuleToComplete(String moduleId) {
        return incompleteModules.size() == 1 && incompleteModules.contains(moduleId);
    }

    public Optional<ModuleRecord> getBookedEventModule() {
        return Optional.ofNullable(bookedEventModule);
    }

}
