package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleRecords {
    public List<ModuleRecord> moduleRecords = new ArrayList<>();

    public ModuleRecords(ModuleRecord moduleRecord) {
        this(List.of(moduleRecord));
    }
}
