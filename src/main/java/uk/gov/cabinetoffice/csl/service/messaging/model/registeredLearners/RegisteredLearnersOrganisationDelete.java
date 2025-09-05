package uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RegisteredLearnersOrganisationDelete {
    private final Long organisationalUnitId;
}
