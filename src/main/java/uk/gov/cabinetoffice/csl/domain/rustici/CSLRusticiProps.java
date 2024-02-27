package uk.gov.cabinetoffice.csl.domain.rustici;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module.ModuleRecordAction;

import java.util.List;

@Data
@RequiredArgsConstructor
public class CSLRusticiProps {
    private final String courseId;
    private final String moduleId;
    private final String learnerId;
    private final List<ModuleRecordAction> moduleRecordActions;
}
