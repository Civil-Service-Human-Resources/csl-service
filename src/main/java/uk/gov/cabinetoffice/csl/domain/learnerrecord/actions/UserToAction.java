package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.User;

@RequiredArgsConstructor
@Data
public class UserToAction<A extends ICourseRecordActionType> {
    private final User user;
    private final A action;
}
