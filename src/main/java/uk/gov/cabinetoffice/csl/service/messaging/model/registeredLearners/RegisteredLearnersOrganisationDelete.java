package uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class RegisteredLearnersOrganisationDelete {
    private final List<Long> organisationIds;
}
