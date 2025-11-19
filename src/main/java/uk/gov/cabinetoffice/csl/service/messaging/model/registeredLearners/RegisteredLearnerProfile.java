package uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RegisteredLearnerProfile {
    private final String uid;
    private final String email;
    private final String fullName;
    private final Long organisationId;
    private final String organisationName;
    private final Integer gradeId;
    private final String gradeName;
    private final Integer professionId;
    private final String professionName;
}
