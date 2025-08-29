package uk.gov.cabinetoffice.csl.service.learning;

import lombok.Getter;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

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

    public List<String> getRequiredIdsLeftForCompletion(List<String> moduleIdsRequiredForCourseCompletion) {
        return moduleIdsRequiredForCourseCompletion.stream().filter(incompleteModules::contains).toList();
    }

    public Optional<ModuleRecord> getBookedEventModule() {
        return Optional.ofNullable(bookedEventModule);
    }

    private void addModule(ModuleRecord moduleRecord) {
        if (moduleRecord.getUpdatedAt() != null && moduleRecord.getUpdatedAt().isAfter(getLatestUpdatedDate())) {
            setLatestUpdatedDate(moduleRecord.getUpdatedAt());
        }
        if (moduleRecord.getCompletionDate() != null && moduleRecord.getCompletionDate().isAfter(getLatestCompletionDate())) {
            setLatestCompletionDate(moduleRecord.getCompletionDate());
        }
        if (moduleRecord.getState().equals(State.COMPLETED)) {
            getCompletedModules().add(moduleRecord.getModuleId());
        } else {
            getIncompleteModules().add(moduleRecord.getModuleId());
        }
        if (moduleRecord.isEventModule() && getBookedEventModule().isEmpty()) {
            setBookedEventModule(moduleRecord);
        }
    }

    @Override
    public boolean add(ModuleRecord moduleRecord) {
        this.addModule(moduleRecord);
        return super.add(moduleRecord);
    }
}
