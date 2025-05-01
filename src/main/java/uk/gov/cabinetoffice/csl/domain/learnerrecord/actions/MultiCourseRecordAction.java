package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Data
public class MultiCourseRecordAction implements ICourseRecordActionType {

    private final Collection<IModuleRecordAction> actions;

    @Override
    public String getDescription() {
        return actions.stream().map(IModuleRecordAction::getAction).collect(Collectors.joining(", "));
    }
}
